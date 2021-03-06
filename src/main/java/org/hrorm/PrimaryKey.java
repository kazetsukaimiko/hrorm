package org.hrorm;

/**
 * Representation of the column that holds the primary key for the entity.
 *
 * <p>
 *
 * Most users of hrorm will have no need to directly use this.
 *
 * @param <ENTITY> The type of the entity being represented
 * @param <BUILDER> The type of the class that can construct new <code>ENTITY</code> instances
 */
public interface PrimaryKey<ENTITY, BUILDER> extends Column<ENTITY, BUILDER> {

    /**
     * Sets the key onto the object
     *
     * @param item the object whose key is to be set
     * @param id the primary key to assign it
     */
    void optimisticSetKey(ENTITY item, Long id);

    void setKey(BUILDER builder, Long id);

    Long getKey(ENTITY item);

    /**
     * The name of the database sequence that is used to populate this key
     *
     * @return the sequence name
     */
    String getSequenceName();

}
