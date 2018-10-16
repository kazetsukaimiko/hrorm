package org.hrorm;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DirectTypedColumn<T> extends TypedColumn<T> {

    /**
     * Populates the object with the data read from the database.
     *
     * @param item The object being populated
     * @param resultSet The result said being read
     * @return A coded value indicating information about what happened
     * during population
     * @throws SQLException allowed for <code>ResultSet</code> operations
     */
    PopulateResult populate(T item, ResultSet resultSet) throws SQLException;

}
