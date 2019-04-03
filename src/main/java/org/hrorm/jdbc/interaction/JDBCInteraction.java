package org.hrorm.jdbc.interaction;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public interface JDBCInteraction<TYPE> extends PreparedStatementSetter<TYPE>, ResultSetReader<TYPE> {

    // Numeric
    JDBCInteraction<Integer> INTEGER = construct(ResultSet::getInt, PreparedStatement::setInt);
    JDBCInteraction<Long> LONG = construct(ResultSet::getLong, PreparedStatement::setLong);
    JDBCInteraction<Float> FLOAT = construct(ResultSet::getFloat, PreparedStatement::setFloat);
    JDBCInteraction<Double> DOUBLE = construct(ResultSet::getDouble, PreparedStatement::setDouble);
    JDBCInteraction<BigDecimal> BIG_DECIMAL = construct(ResultSet::getBigDecimal, PreparedStatement::setBigDecimal);

    // Temporal
    JDBCInteraction<Date> DATE = construct(ResultSet::getDate, PreparedStatement::setDate);
    JDBCInteraction<Timestamp> TIMESTAMP = construct(ResultSet::getTimestamp, PreparedStatement::setTimestamp);
    JDBCInteraction<Time> TIME = construct(ResultSet::getTime, PreparedStatement::setTime);

    // Symbolic
    JDBCInteraction<String> STRING = construct(ResultSet::getString, PreparedStatement::setString);
    JDBCInteraction<Boolean> BOOLEAN = construct(ResultSet::getBoolean, PreparedStatement::setBoolean);

    static <TYPE> JDBCInteraction<TYPE> construct(final ResultSetReader<TYPE> getter, final PreparedStatementSetter<TYPE> setter) {
        return new JDBCInteraction<TYPE>() {
            @Override
            public void apply(PreparedStatement preparedStatement, int index, TYPE type) throws SQLException {
                setter.apply(preparedStatement, index, type);
            }

            @Override
            public TYPE read(ResultSet resultSet, String columnName) throws SQLException {
                return getter.read(resultSet, columnName);
            }
        };
    }
}
