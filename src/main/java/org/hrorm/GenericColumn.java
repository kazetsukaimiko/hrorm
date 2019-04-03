package org.hrorm;

import org.hrorm.jdbc.interaction.JDBCInteraction;
import org.hrorm.jdbc.interaction.PreparedStatementSetter;
import org.hrorm.jdbc.interaction.ResultSetReader;
import org.hrorm.jdbc.types.ColumnType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private final Integer sqlType;
    private final String sqlTypeName;

    // private final ColumnType<TYPE> columnType; // TODO, as to replace the above.
    private final JDBCInteraction<TYPE> jdbcInteraction;

    /**
     * Create a generic column instance to support the <code>TYPE</code>.
     *
     * @param jdbcInteraction The jdbc interaction behavior.
     * @param sqlType The kind of this column type, as defined in <code>java.sql.Types</code>
     */
    public GenericColumn(JDBCInteraction<TYPE> jdbcInteraction, int sqlType){
        this.sqlType = sqlType;
        this.jdbcInteraction = jdbcInteraction;
        this.sqlTypeName = "UNSET";
    }

    /**
     * Create a generic column instance to support the <code>TYPE</code>.
     *
     * @param jdbcInteraction The jdbc interaction behavior.
     * @param sqlType The kind of this column type, as defined in <code>java.sql.Types</code>
     * @param sqlTypeName The name of the type in the SQL schema. This optional value can be set
     *                    if you wish to generate your schema using a {@link Schema} object.
     */
    public GenericColumn(JDBCInteraction<TYPE> jdbcInteraction, int sqlType, String sqlTypeName){
        this.sqlType = sqlType;
        this.jdbcInteraction = jdbcInteraction;
        this.sqlTypeName = sqlTypeName;
    }

    public TYPE fromResultSet(ResultSet resultSet, String columnName) throws SQLException {
        TYPE value = jdbcInteraction.read(resultSet, columnName);
        if( resultSet.wasNull() ){
            return null;
        }
        return value;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement, int index, TYPE value) throws SQLException {
        jdbcInteraction.apply(preparedStatement, index, value);
    }

    public int sqlType() {
        return sqlType;
    }

    public String getSqlTypeName(){
        return sqlTypeName;
    }

}
