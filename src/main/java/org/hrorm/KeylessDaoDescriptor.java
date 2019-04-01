package org.hrorm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementers of this interface completely describe all the information
 * necessary to persisting objects of type <code>ENTITY</code>, except for
 * the primary key.
 *
 * <p>
 *     See also: {@link DaoDescriptor}
 * </p>
 *
 * Most users of hrorm will have no need to directly use this.
 *
 * @param <ENTITY> The type representing the enitity being persisted.
 * @param <ENTITYBUILDER> The type of object that can build an <code>ENTITY</code> instance.
 */
public interface KeylessDaoDescriptor<ENTITY, ENTITYBUILDER> {

    /**
     * The name of the table that is used to persist type <code>ENTITY</code>
     *
     * @return the table name
     */
    String tableName();

    /**
     * The mechanism to use to instantiate a new instance of type <code>ENTITY</code>,
     * generally a no-argument constructor of the class.
     *
     * @return A function pointer to the instantiation mechanism
     */
    Supplier<ENTITYBUILDER> supplier();

    /**
     * The columns that contain the data that make up the object
     *
     * @return all the data columns supported
     */
    List<Column<ENTITY, ENTITYBUILDER>> dataColumns();

    /**
     * The columns that contain references to foreign keys to other objects
     *
     * @return all the reference columns supported
     */
    List<JoinColumn<ENTITY, ?, ENTITYBUILDER, ?>> joinColumns();

    // FIXME: This should be on the DaoDescriptor interface

    /**
     * The definitions of any entities that are owned by type <code>ENTITY</code>
     *
     * @return all the owned entities
     */
    List<ChildrenDescriptor<ENTITY, ?, ENTITYBUILDER, ?>> childrenDescriptors();

    Function<ENTITYBUILDER, ENTITY> buildFunction();

    /**
     * All the columns of the underlying table, both data type and join type.
     *
     * @return all the columns
     */
    default List<Column<ENTITY, ENTITYBUILDER>> allColumns() {
        List<Column<ENTITY, ENTITYBUILDER>> allColumns = new ArrayList<>();
        allColumns.addAll(dataColumns());
        allColumns.addAll(joinColumns());
        return Collections.unmodifiableList(allColumns);
    }

    default ColumnSelection<ENTITY, ENTITYBUILDER> select(String... columnNames) {
        return new ColumnSelection(allColumns(), columnNames);
    }

    // FIXME: this method is named wrong and should perhaps not be here?
    List<Column<ENTITY, ENTITYBUILDER>> dataColumnsWithParent();
}
