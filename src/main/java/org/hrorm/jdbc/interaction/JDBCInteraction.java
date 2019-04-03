package org.hrorm.jdbc.interaction;

import org.hrorm.jdbc.types.BigDecimalType;
import org.hrorm.jdbc.types.BooleanType;
import org.hrorm.jdbc.types.ColumnType;
import org.hrorm.jdbc.types.DateType;
import org.hrorm.jdbc.types.DoubleType;
import org.hrorm.jdbc.types.FloatType;
import org.hrorm.jdbc.types.IntegerType;
import org.hrorm.jdbc.types.LongType;
import org.hrorm.jdbc.types.StringType;
import org.hrorm.jdbc.types.TimeType;
import org.hrorm.jdbc.types.TimestampType;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

public class JDBCInteraction<TYPE> implements PreparedStatementSetter<TYPE>, ResultSetReader<TYPE> {

    private final ResultSetReader<TYPE> getter;
    private final PreparedStatementSetter<TYPE> setter;
    private final ColumnType<TYPE> columnType;

    private JDBCInteraction(ResultSetReader<TYPE> getter, PreparedStatementSetter<TYPE> setter, ColumnType<TYPE> columnType) {
        this.getter = getter;
        this.setter = setter;
        this.columnType = columnType;
    }

    // Numeric
    public static JDBCInteraction<Integer> jdbcInteger(IntegerType integerType) {
        return new JDBCInteraction<>(ResultSet::getInt, PreparedStatement::setInt, integerType);
    }
    public static JDBCInteraction<Long> jdbcLong(LongType longType) {
        return new JDBCInteraction<>(ResultSet::getLong, PreparedStatement::setLong, longType);
    }
    public static JDBCInteraction<Float> jdbcFloat(FloatType floatType) {
        return new JDBCInteraction<>(ResultSet::getFloat, PreparedStatement::setFloat, floatType);
    }
    public static JDBCInteraction<Double> jdbcDouble(DoubleType doubleType) {
        return new JDBCInteraction<>(ResultSet::getDouble, PreparedStatement::setDouble, doubleType);
    }
    public static JDBCInteraction<BigDecimal> jdbcBigDecimal(BigDecimalType bigDecimalType) {
        return new JDBCInteraction<>(ResultSet::getBigDecimal, PreparedStatement::setBigDecimal, bigDecimalType);
    }

    // Temporal
    public static JDBCInteraction<Date> jdbcDate(DateType dateType) {
        return new JDBCInteraction<>(ResultSet::getDate, PreparedStatement::setDate, dateType);
    }

    public static JDBCInteraction<Timestamp> jdbcTimestamp(TimestampType timestampType) {
        return new JDBCInteraction<>(ResultSet::getTimestamp, PreparedStatement::setTimestamp, timestampType);
    }
    public static JDBCInteraction<Time> jdbcTime(TimeType timeType) {
        return new JDBCInteraction<>(ResultSet::getTime, PreparedStatement::setTime, timeType);
    }

    // Symbolic
    public static JDBCInteraction<String> jdbcString(StringType stringType) {
        return new JDBCInteraction<>(ResultSet::getString, PreparedStatement::setString, stringType);
    }
    public static JDBCInteraction<Boolean> jdbcBoolean(BooleanType booleanType) {
        return new JDBCInteraction<>(ResultSet::getBoolean, PreparedStatement::setBoolean, booleanType);
    }

    @Override
    public void apply(PreparedStatement preparedStatement, int index, TYPE type) throws SQLException {
        setter.apply(preparedStatement, index, type);
    }

    @Override
    public TYPE read(ResultSet resultSet, String columnName) throws SQLException {
        return getter.read(resultSet, columnName);
    }

    public ColumnType<TYPE> getColumnType() {
        return columnType;
    }
}
