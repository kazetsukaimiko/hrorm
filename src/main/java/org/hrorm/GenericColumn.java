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
    private final JDBCInteraction<TYPE> jdbcInteraction;


    /**
     * Create a generic column instance to support the <code>TYPE</code>.
     *
     * @param jdbcInteraction The jdbc interaction behavior.
     */
    public GenericColumn(JDBCInteraction<TYPE> jdbcInteraction){
        this.jdbcInteraction = jdbcInteraction;
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
        return jdbcInteraction.getColumnType().getSqlType();
    }

    public String getSqlTypeName(){
        return jdbcInteraction.getColumnType().getSqlTypeName();
    }

}
