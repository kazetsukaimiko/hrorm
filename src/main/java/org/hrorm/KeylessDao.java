package org.hrorm;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A <code>KeylessDao</code> is an interface that allows basic, non-singular CRUD operations to be performed.
 * Using a <code>KeylessDao</code>, you can insert, records individually, but select, update, and delete all are limited
 * to multi-row operations, as there is no Primary Key defined.
 *
 * @param <ENTITY> The type of the data to be persisted.
 */
public interface KeylessDao<ENTITY> {

    /**
     * Insert a record into the database.
     *
     * <p>Depending on how the Dao was constucted (whether from a regular
     * <code>DaoBuilder</code> or an <code>IndirectDaoBuilder</code>)
     * a particular instance of this class may or may not attempt
     * to mutate the state of the passed item by setting its primary
     * key.</p>
     *
     * @param item The instance to be inserted.
     * @return The newly issued primary key of the record, if there is one. Else, Optional.empty()
     */
    Long insert(ENTITY item);


    /**
     * Read all the records in the database of type ENTITY.
     *
     * <p>No laziness or caching is involved here. This simply tries to
     * instantiate all the records it can based on the full table.</p>
     *
     * @return A list of populated instances of type ENTITY.
     */
    List<ENTITY> selectAll();

    /**
     * Read all the records in the database of type ENTITY in the
     * required order.
     *
     * <p>No laziness or caching is involved here. This simply tries to
     * instantiate all the records it can based on the full table.</p>
     *
     * @param order The ordering to use
     * @return A list of populated instances of type ENTITY.
     */
    List<ENTITY> selectAll(Order order);

    /**
     * Select multiple records from the database by some search criteria.
     *
     * <p>The SQL generated will specify a select by the column names passed,
     * where the values are equal to the values specified in the passed template
     * object. All the values must match, as the where clause will be formed
     * by joining the various column names with 'AND'.
     * </p>
     *
     * @param template An instance of type ENTITY with populated values corresponding to the
     *             column names to select by.
     * @param columnNames The names of the database columns
     * @return The populated instances of type ENTITY with matching values with the passed item for
     *         the indicated columnNames.
     */
    List<ENTITY> selectManyByColumns(ENTITY template, String... columnNames);

    /**
     * Select multiple records from the database by some search criteria in the
     * required order.
     *
     * <p>The SQL generated will specify a select by the column names passed,
     * where the values are equal to the values specified in the passed template
     * object. All the values must match, as the where clause will be formed
     * by joining the various column names with 'AND'.
     * </p>
     *
     * @param template An instance of type ENTITY with populated values corresponding to the
     *             column names to select by.
     * @param order The ordering to use
     * @param columnNames The names of the database columns
     * @return The populated instances of type ENTITY with matching values with the passed item for
     *         the indicated columnNames.
     */
    List<ENTITY> selectManyByColumns(ENTITY template, Order order, String... columnNames);


    /**
     * Select a single record from the database by some search criteria.
     *
     * If multiple records are found that match the passed item, an exception will be thrown.
     *
     * @param item An instance of type ENTITY with populated values corresponding to the
     *             column names to select by.
     * @param columnNames The names of the database columns
     * @return The populated instance of type ENTITY with matching values with the passed item for
     *         the indicated columnNames.
     */
    ENTITY selectByColumns(ENTITY item, String... columnNames);

    /**
     * Run a select in the data store for entities matching the given where predicates.
     *
     * @param where The predicates to drive selection.
     * @return The matching results.
     */
    List<ENTITY> select(Where where);


    /**
     * Run a select in the data store for entities matching the given where predicates
     * returned in the order specified.
     *
     * @param where The predicates to drive selection.
     * @param order The ordering to use
     * @return The matching results.
     */
    List<ENTITY> select(Where where, Order order);

    /**
     * Insert a record into the database within a transaction that is
     * managed within the Dao. The Dao will either commit or rollback
     * the transaction and <b>close the underlying <code>Connection</code></b>
     * when complete.
     *
     * @param item The instance to be inserted.
     * @return The newly issued primary key of the record, if there is one. Else, Optional.empty()
     */
    Long atomicInsert(ENTITY item);

    /**
     * Computes a result based on the entities found by a select statement
     * without realizing the entire list of found entities in memory.
     *
     * @param identity The identity element of the return type.
     * @param accumulator A function that computes the desired value based on
     *                    the values seen thus far and the next instance
     *                    of the entity found in the result set.
     * @param where Predicates to drive selection of results
     * @param <T> The type of the value to be computed.
     * @return The computed value based on the results found in the underlying store.
     */
    <T> T foldingSelect(T identity, BiFunction<T,ENTITY,T> accumulator, Where where);


    /**
     * Computes an aggregated Long value, based on the select criteria specified
     * and the given SqlFunction and column name.
     *
     * <p>
     *     Will run SQL that looks like this:
     * </p>
     *
     * <code>
     *     select FUNCTION(COLUMN) from TABLE where ...
     * </code>
     *
     * @param function The function to run
     * @param columnName The column to apply the function to
     * @param where Predicates to drive selection of results
     * @return The value returned by applying the specified function to the values in the
     * specified column in the matching results, or null if no results are found.
     */
    Long runLongFunction(SqlFunction function, String columnName, Where where);

    /**
     * Computes an aggregated BigDecimal value, based on the select criteria specified
     * and the given SqlFunction and column name.
     *
     * <p>
     *     Will run SQL that looks like this:
     * </p>
     *
     * <code>
     *     select FUNCTION(COLUMN) from TABLE where ...
     * </code>
     *
     * @param function The function to run
     * @param columnName The column to apply the function to
     * @param where Predicates to drive selection of results
     * @return The value returned by applying the specified function to the values in the
     * specified column in the matching results, or null if no results are found.
     */
    BigDecimal runBigDecimalFunction(SqlFunction function, String columnName, Where where);

}
