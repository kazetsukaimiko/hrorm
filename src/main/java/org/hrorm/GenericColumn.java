package org.hrorm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * A column that represents a particular Java type.
 *
 * <p>
 *     This can be used if none of the types Hrorm has built in meet
 *     the needs. The user must provide mechanisms for setting the
 *     type's value onto a <code>java.sql.PreparedStatement</code> and
 *     for reading a value from a <code>java.sql.ResultSet</code>.
 * </p>
 *
 * <p>
 *      For example, to create a column for <code>Integer</code>, do the following:
 * </p>
 *
 * <pre>{@code
 * GenericColumn<Integer> integerColumn = new GenericColumn<>(
 *     PreparedStatement::setInt,
 *     ResultSet::getInt,
 *     java.sql.Types.Integer);
 * }</pre>
 *
 * @param <TYPE> The Java type represented by the column.
 */
public class GenericColumn<TYPE> {

    public static GenericColumn<Integer> INTEGER =
            new GenericColumn<>(PreparedStatement::setInt, ResultSet::getInt, Types.INTEGER, "integer");
    public static GenericColumn<Byte> BYTE =
            new GenericColumn<>(PreparedStatement::setByte, ResultSet::getByte, Types.TINYINT, "tinyint");
    public static GenericColumn<Float> FLOAT =
            new GenericColumn<>(PreparedStatement::setFloat, ResultSet::getFloat, Types.DOUBLE, "float");
    public static GenericColumn<Double> DOUBLE =
            new GenericColumn<>(PreparedStatement::setDouble, ResultSet::getDouble, Types.DOUBLE, "double");
    public static GenericColumn<Timestamp> TIMESTAMP =
            new GenericColumn<>(PreparedStatement::setTimestamp, ResultSet::getTimestamp, Types.TIMESTAMP, "timestamp");
//    public static GenericColumn<Time> TIME =
//            new GenericColumn<>(PreparedStatement::setTime, ResultSet::getTime, Types.TIME, "time");

    private final Integer sqlType;
    private final String sqlTypeName;
    private final PreparedStatementSetter<TYPE> preparedStatementSetter;
    private final ResultSetReader<TYPE> resultReader;

    /**
     * Create a generic column instance to support the <code>TYPE</code>.
     *
     * @param preparedStatementSetter The method used to set the type onto a prepared statement.
     * @param resultReader The method used to read the value out of a result set.
     * @param sqlType The kind of this column type, as defined in <code>java.sql.Types</code>
     */
    public GenericColumn(PreparedStatementSetter<TYPE> preparedStatementSetter, ResultSetReader<TYPE> resultReader, int sqlType){
        this.sqlType = sqlType;
        this.preparedStatementSetter = preparedStatementSetter;
        this.resultReader = resultReader;
        this.sqlTypeName = "UNSET";
    }

    /**
     * Create a generic column instance to support the <code>TYPE</code>.
     *
     * @param preparedStatementSetter The method used to set the type onto a prepared statement.
     * @param resultReader The method used to read the value out of a result set.
     * @param sqlType The kind of this column type, as defined in <code>java.sql.Types</code>
     * @param sqlTypeName The name of the type in the SQL schema. This optional value can be set
     *                    if you wish to generate your schema using a {@link Schema} object.
     */
    public GenericColumn(PreparedStatementSetter<TYPE> preparedStatementSetter, ResultSetReader<TYPE> resultReader, int sqlType, String sqlTypeName){
        this.sqlType = sqlType;
        this.preparedStatementSetter = preparedStatementSetter;
        this.resultReader = resultReader;
        this.sqlTypeName = sqlTypeName;
    }

    public TYPE fromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        TYPE value = resultReader.read(resultSet, columnName);
        if( resultSet.wasNull() ){
            return null;
        }
        return value;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement, int index, TYPE value) throws SQLException {
        preparedStatementSetter.apply(preparedStatement, index, value);
    }

    public int sqlType() {
        return sqlType;
    }

    public String getSqlTypeName(){
        return sqlTypeName;
    }

}
