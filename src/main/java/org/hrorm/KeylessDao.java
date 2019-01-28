package org.hrorm;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A <code>Dao</code> is an interface that allows basic CRUD operations to be performed.
 * Using a <code>Dao</code>, you can insert, select, update, and delete records.
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
    Optional<Long> insert(ENTITY item);


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
     * Select multiple records from the database by some search criteria.
     *
     * @param item An instance of type ENTITY with populated values corresponding to the
     *             column names to select by.
     * @param columnNames The names of the database columns
     * @return The populated instances of type ENTITY with matching values with the passed item for
     *         the indicated columnNames.
     */
    List<ENTITY> selectManyByColumns(ENTITY item, String... columnNames);

    /**
     * Deletes multiple records from the database by some search criteria.
     *
     * @param item An instance of type ENTITY with populated values corresponding to the
     *             column names to select by.
     * @param columnNames The names of the database columns
     *
     */
    List<ENTITY> deleteManyByColumns(ENTITY item, String... columnNames);

    /**
     * Update multiple records from the database by some search criteria.
     *
     * @param selectionItem An instance of type ENTITY with populated values corresponding to the
     *             column names to select by.
     * @param selectionColumnNames The names of the database columns to select by.
     *
     * @param updateItem An instance of type ENTITY with populated values corresponding to the
     *             column names to update to.
     * @param updateColumnNames The names of the database columns to update.
     */
    List<ENTITY> updateManyByColumns(ENTITY selectionItem, String[] selectionColumnNames, ENTITY updateItem, String[] updateColumnNames);

    /**
     * Insert a record into the database within a transaction that is
     * managed within the Dao. The Dao will either commit or rollback
     * the transaction and <b>close the underlying <code>Connection</code></b>
     * when complete.
     *
     * @param item The instance to be inserted.
     * @return The newly issued primary key of the record, if there is one. Else, Optional.empty()
     */
    Optional<Long> atomicInsert(ENTITY item);


    /**
     * Computes a result based on the entities found by a select statement
     * without realizing the entire list of found entities in memory.
     *
     * @param template An instance of type ENTITY with populated values corresponding to the
     *                column names to select by.
     * @param identity The identity element of the return type.
     * @param accumulator A function that computes the desired value based on
     *                    the values seen thus far and the next instance
     *                    of the entity found in the result set.
     * @param columnNames The names of the columns to include in the select where clause.
     * @param <T> The type of the value to be computed.
     * @return The computed value based on the results found in the underlying store.
     */
    <T> T foldingSelect(ENTITY template, T identity, BiFunction<T,ENTITY,T> accumulator, String ... columnNames);

    /**
     * Access the <code>SQL</code> this <code>Dao</code> is using.
     *
     * <p>The <code>SQL</code> provided is suitable for being passed to a
     * <code>PreparedStatement</code>.</p>
     *
     * @return An object containing the literal <code>SQL</code>.
     */
    Queries queries();

}
