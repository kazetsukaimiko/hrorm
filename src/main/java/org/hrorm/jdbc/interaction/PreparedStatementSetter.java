package org.hrorm.jdbc.interaction;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementSetter<VALUE> {
    void apply(PreparedStatement preparedStatement, int index, VALUE value) throws SQLException;
}
