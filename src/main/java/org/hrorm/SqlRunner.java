package org.hrorm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class does the heavy lifting of creating <code>Statement</code>s,
 * executing SQL, and parsing <code>ResultSet</code>s.
 *
 * <p>
 *
 * Most users of hrorm will have no need to directly use this.
 *
 * @param <ENTITY> the type of object this runner supports
 * @param <BUILDER> the type of object that can construct new <code>ENTITY</code> instances
 */
public class SqlRunner<ENTITY, BUILDER> {

    private static final Logger logger = Logger.getLogger("org.hrorm");

    private final Connection connection;
    private final List<Column<ENTITY, BUILDER>> allColumns;

    public SqlRunner(Connection connection, KeylessDaoDescriptor<ENTITY, BUILDER> daoDescriptor) {
        this.connection = connection;
        this.allColumns = daoDescriptor.allColumns();
    }

    public Stream<BUILDER> select(String sql, Supplier<BUILDER> supplier, List<ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors){
        return selectByColumns(sql, supplier, ColumnSelection.empty(), childrenDescriptors, null);
    }

    public Stream<BUILDER> selectByColumns(String sql,
                                         Supplier<BUILDER> supplier,
                                         ColumnSelection<ENTITY,BUILDER> columnSelection,
                                         List<? extends ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors,
                                         ENTITY item){
        return stream(sql, columnSelection.buildPopulator(item), supplier, childrenDescriptors);
    }

    public Stream<BUILDER> selectWhere(String sql,
                                     Supplier<BUILDER> supplier,
                                     List<? extends ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors,
                                     Where where){
        return stream(sql, where, supplier, childrenDescriptors);
    }

    public <T,X> T foldingSelect(String sql,
                               StatementPopulator statementPopulator,
                               Supplier<BUILDER> supplier,
                               List<? extends ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors,
                               Function<BUILDER, X> buildFunction,
                               T identity,
                               BiFunction<T,X,T> accumulator){
        return stream(sql, statementPopulator, supplier, childrenDescriptors)
                .map(buildFunction)
                .reduce(identity, accumulator, (a, b) -> a);
    }

    public Stream<BUILDER> stream(String sql,
                                     StatementPopulator statementPopulator,
                                     Supplier<BUILDER> supplier,
                                     List<? extends ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors){
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            // Closes all resources after the Stream closes.
            UncheckedCloseable close = UncheckedCloseable.wrap(statement);

            statementPopulator.populate(statement);

            logger.info(sql);
            ResultSet resultSet = statement.executeQuery();
            close = close.nest(resultSet);
            return StreamSupport.stream(new Spliterators.AbstractSpliterator<BUILDER>(Long.MAX_VALUE, Spliterator.ORDERED){
                @Override
                public boolean tryAdvance(Consumer<? super BUILDER> consumer) {
                    try {
                        if (resultSet.isClosed() || !resultSet.next()) return false;
                        BUILDER bldr = populate(resultSet, supplier);
                        for(ChildrenDescriptor<ENTITY,?, BUILDER,?> descriptor : childrenDescriptors){
                            descriptor.populateChildren(connection, bldr);
                        }
                        if (bldr == null) return false;
                        consumer.accept(bldr);
                        return true;
                    } catch (SQLException e) {
                        throw new HrormException(e);
                    }
                }
            }, false).onClose(close);
        } catch (SQLException ex){
            throw new HrormException(ex, sql);
        }
    }


    private <T> T runFunction(String sql,
                              Where where,
                              Function<ResultSet, T> reader) {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            where.populate(statement);

            logger.info(sql);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return reader.apply(resultSet);
            } else {
                return null;
            }
        } catch (SQLException ex){
            throw new HrormException(ex, sql);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se){
                throw new HrormException(se);
            }
        }
    }

    public BigDecimal runBigDecimalFunction(String sql,
                                            Where where) {
        Function<ResultSet, BigDecimal> reader = resultSet -> {
            try {
                return resultSet.getBigDecimal(1);
            } catch (SQLException ex){
                throw new HrormException(ex, sql);
            }
        };
        return runFunction(sql, where, reader);
    }

    public Long runLongFunction(String sql,
                                Where where) {
        Function<ResultSet, Long> reader = resultSet -> {
            try {
                return resultSet.getLong(1);
            } catch (SQLException ex){
                throw new HrormException(ex, sql);
            }
        };
        return runFunction(sql, where, reader);
    }

    public void insert(String sql, Envelope<ENTITY> envelope) {
        runInsertOrUpdate(sql, envelope, false);
    }

    public void update(String sql, Envelope<ENTITY> envelope) {
        runInsertOrUpdate(sql, envelope, true);
    }

    private void runInsertOrUpdate(String sql, Envelope<ENTITY> envelope, boolean isUpdate){

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            int idx = 1;
            for(Column<ENTITY, BUILDER> column : allColumns){
                if( column.isPrimaryKey() ) {
                    if ( ! isUpdate ) {
                        preparedStatement.setLong(idx, envelope.getId());
                        idx++;
                    }
                } else if ( column.isParentColumn() ){
                    preparedStatement.setLong(idx, envelope.getParentId());
                    idx++;
                } else if ( ! column.isPrimaryKey()  ){
                    column.setValue(envelope.getItem(), idx, preparedStatement);
                    idx++;
                }
            }
            if( isUpdate ){
                preparedStatement.setLong(idx, envelope.getId());
            }

            logger.info(sql);
            preparedStatement.execute();

        } catch (SQLException se){
            throw new HrormException(se, sql);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                throw new HrormException(se);
            }
        }

    }

    private BUILDER populate(ResultSet resultSet, Supplier<BUILDER> supplier)
            throws SQLException {
        BUILDER item = supplier.get();

        for (Column<ENTITY, BUILDER> column: allColumns) {
            PopulateResult populateResult = column.populate(item, resultSet);
            populateResult.populateChildren(connection);
        }

        return item;
    }
}
