package org.hrorm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetQuery implements AutoCloseable {
    private final Connection connection;
    private final String sql;
    private final StatementPopulator statementPopulator;

    private PreparedStatement statement; // = connection.prepareStatement(sql);
    private ResultSet resultSet;
    private UncheckedCloseable close = UncheckedCloseable.noop();

    public ResultSetQuery(Connection connection, String sql, StatementPopulator statementPopulator) {
        this.connection = connection;
        this.sql = sql;
        this.statementPopulator = statementPopulator;
    }

    private ResultSet getResultSet() throws SQLException {
        if (resultSet == null || resultSet.isClosed()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            // Closes all resources after the Stream closes.
            close = UncheckedCloseable.wrap(statement);

            statementPopulator.populate(statement);

            resultSet = statement.executeQuery();
            close = close.nest(resultSet);
        }
        return resultSet;
    }


    public Stream<ResultSet> stream() {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ResultSet>(Long.MAX_VALUE, Spliterator.ORDERED){
            @Override
            public boolean tryAdvance(Consumer<? super ResultSet> consumer) {
                try {
                    if (getResultSet().isClosed() || !getResultSet().next()) return false;
                    consumer.accept(getResultSet());
                    return true;
                } catch (SQLException e) {
                    throw new HrormException(e);
                }
            }
        }, false).onClose(close);
    }

    @Override
    public void close() throws Exception {
        if (close != null) {
            close.close();
        }
    }
}
