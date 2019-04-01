package org.hrorm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementers of this interface completely describe all the information
 * necessary to persisting objects of type <code>ENTITY</code>.
 *
 * <p>
 *
 * Most users of hrorm will have no need to directly use this.
 *
 * @param <ENTITY> The type representing the enitity being persisted.
 * @param <ENTITYBUILDER> The type of object that can build an <code>ENTITY</code> instance.
 */
public interface DaoDescriptor<ENTITY, ENTITYBUILDER> extends KeylessDaoDescriptor<ENTITY, ENTITYBUILDER> {

    /**
     * The primary key for objects of type <code>ENTITY</code>
     *
     * @return the primary key
     */
    PrimaryKey<ENTITY, ENTITYBUILDER> primaryKey();

    <P,PB> ParentColumn<ENTITY, P, ENTITYBUILDER, PB> parentColumn();

    default boolean hasParent(){
        return parentColumn() != null;
    }

    default List<Column<ENTITY, ENTITYBUILDER>> dataColumnsWithParent(){
        return dataColumnsWithParent(dataColumns(), parentColumn(), hasParent());
    }

    static <ENTITY, ENTITYBUILDER, P, PB> List<Column<ENTITY, ENTITYBUILDER>> dataColumnsWithParent(
            List<Column<ENTITY, ENTITYBUILDER>> dataColumns, ParentColumn<ENTITY, P, ENTITYBUILDER, PB> parentColumn,
            boolean hasParent){
        List<Column<ENTITY, ENTITYBUILDER>> allColumns = new ArrayList<>(dataColumns);
        if ( hasParent ) {
            allColumns.add(parentColumn);
        }
        return Collections.unmodifiableList(allColumns);
    }

}
