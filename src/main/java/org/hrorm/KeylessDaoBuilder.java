package org.hrorm;

import javafx.util.Builder;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A KeylessDaoBuilder provides mechanisms for defining the relationship between
 * a Java type and the table(s) that will persist the data held in the class, where the
 * specified table does not have a unique Primary Key. This allows for operations on a table that
 * do not require a unique key.
 *
 * <p>
 *     Also see {@link DaoBuilder} for tables with Primary Keys, and {@link IndirectDaoBuilder}.
 * </p>
 *
 * @param <ENTITY> The class that the Dao will support.
 */
public class KeylessDaoBuilder<ENTITY, BUILDER> implements KeylessDaoDescriptor<ENTITY, BUILDER> {

    protected final IndirectDaoBuilder<ENTITY, BUILDER> internalDaoBuilder;

    public KeylessDaoBuilder(String table, Supplier<BUILDER> supplier, Function<BUILDER, ENTITY> buildFunction) {
        this.internalDaoBuilder = new IndirectDaoBuilder<>(table, supplier, buildFunction);
    }

    public KeylessDaoBuilder(IndirectDaoBuilder.BuilderHolder<ENTITY, BUILDER> builderHolder){
        this.internalDaoBuilder = builderHolder.daoBuilder;
    }

    @Override
    public String tableName() {
        return internalDaoBuilder.tableName();
    }

    @Override
    public Supplier<BUILDER> supplier() {
        return internalDaoBuilder.supplier();
    }

    @Override
    public List<Column<ENTITY, BUILDER>> dataColumns() {
        return internalDaoBuilder.dataColumns();
    }

    @Override
    public List<ChildrenDescriptor<ENTITY, ?, BUILDER, ?>> childrenDescriptors() {
        return internalDaoBuilder.childrenDescriptors();
    }

    @Override
    public ParentColumn<ENTITY, ?, BUILDER, ?> parentColumn() {
        return internalDaoBuilder.parentColumn();
    }

    public List<JoinColumn<ENTITY, ?, BUILDER, ?>> joinColumns() { return internalDaoBuilder.joinColumns(); }

    @Override
    public Function<BUILDER, ENTITY> buildFunction() {
        return internalDaoBuilder.buildFunction();
    }

    /**
     * Creates a {@link Dao} for performing CRUD operations of type <code>ENTITY</code>.
     *
     * @param connection The SQL connection this <code>Dao</code> will use
     *                   for its operations.
     * @return The newly created <code>Dao</code>.
     */
    public KeylessDao<ENTITY> buildDao(Connection connection){
        return internalDaoBuilder.buildKeylessDao(connection);
    }

    /**
     * Build the SQL that will be used by <code>DAO</code> objects created by this builder.
     *
     * @return A container for the SQL
     */
    public Queries buildQueries() { return internalDaoBuilder.buildQueries(); }

    /**
     * Describes a text or string data element.
     *
     * @param columnName The name of the column that holds the data element.
     * @param getter The function on <code>ENTITY</code> that returns the data element.
     * @param setter The function on <code>ENTITY</code> that consumes the data element.
     * @return This instance.
     */
    public KeylessDaoBuilder<ENTITY, BUILDER> withStringColumn(String columnName, Function<ENTITY, String> getter, BiConsumer<BUILDER, String> setter){
        internalDaoBuilder.withStringColumn(columnName, getter, setter);
        return this;
    }

    /**
     * Describes a numeric data element with no decimal or fractional part.
     *
     * @param columnName The name of the column that holds the data element.
     * @param getter The function on <code>ENTITY</code> that returns the data element.
     * @param setter The function on <code>ENTITY</code> that consumes the data element.
     * @return This instance.
     */
    public KeylessDaoBuilder<ENTITY, BUILDER> withIntegerColumn(String columnName, Function<ENTITY, Long> getter, BiConsumer<BUILDER, Long> setter){
        internalDaoBuilder.withIntegerColumn(columnName, getter, setter);
        return this;
    }

    /**
     * Describes a numeric data element with a decimal part.
     *
     * @param columnName The name of the column that holds the data element.
     * @param getter The function on <code>ENTITY</code> that returns the data element.
     * @param setter The function on <code>ENTITY</code> that consumes the data element.
     * @return This instance.
     */
    public KeylessDaoBuilder<ENTITY, BUILDER> withBigDecimalColumn(String columnName, Function<ENTITY, BigDecimal> getter, BiConsumer<BUILDER, BigDecimal> setter){
        internalDaoBuilder.withBigDecimalColumn(columnName, getter, setter);
        return this;
    }

    /**
     * Describes a data element with a particular type (like an enumeration) that
     * is persisted using a <code>String</code> representation.
     *
     * @param columnName The name of the column that holds the data element.
     * @param getter The function on <code>ENTITY</code> that returns the data element.
     * @param setter The function on <code>ENTITY</code> that consumes the data element.
     * @param converter A mechanism for converting between a <code>String</code> and
     *                  the type <code>E</code> that the object contains.
     * @param <E> The type being converted for persistence.
     * @return This instance.
     */
    public <E> KeylessDaoBuilder<ENTITY, BUILDER> withConvertingStringColumn(String columnName, Function<ENTITY, E> getter, BiConsumer<BUILDER, E> setter, Converter<E, String> converter){
        internalDaoBuilder.withConvertingStringColumn(columnName, getter, setter, converter);
        return this;
    }

    /**
     * Describes a data element that represents a time stamp.
     *
     * @param columnName The name of the column that holds the data element.
     * @param getter The function on <code>ENTITY</code> that returns the data element.
     * @param setter The function on <code>ENTITY</code> that consumes the data element.
     * @return This instance.
     */
    public KeylessDaoBuilder<ENTITY, BUILDER> withLocalDateTimeColumn(String columnName, Function<ENTITY, LocalDateTime> getter, BiConsumer<BUILDER, LocalDateTime> setter){
        internalDaoBuilder.withLocalDateTimeColumn(columnName, getter, setter);
        return this;
    }

    /**
     * Describes a data element that represents a true/false value. Boolean
     * elements are persisted to a text column with the single character
     * "ENTITY" or "F".
     *
     * @param columnName The name of the column that holds the data element.
     * @param getter The function on <code>ENTITY</code> that returns the data element.
     * @param setter The function on <code>ENTITY</code> that consumes the data element.
     * @return This instance.
     */
    public KeylessDaoBuilder<ENTITY, BUILDER> withBooleanColumn(String columnName, Function<ENTITY, Boolean> getter, BiConsumer<BUILDER, Boolean> setter){
        internalDaoBuilder.withBooleanColumn(columnName, getter, setter);
        return this;
    }

    /**
     * <p>Describes a data element that is represented by an <code>Object</code> of some
     * other type <code>U</code> with its own table for persistence.</p>
     *
     * <p>Join columns describe entities that have their own independent existence and
     * their persistence is a pre-requisite for the persistence of dependent objects.</p>
     *
     * <p>Imagine a schema that describes cities and states. Every city entity should
     * be assigned to exactly one state. If the city is modified or deleted, it
     * has no repercusions to the state entity. The only thing that can happen is
     * that the city is assigned to a new state.</p>
     *
     * @param columnName The name of the column with the foreign key to the other table.
     *                   This column must be an integer type and must reference the primary
     *                   key of the other table.
     * @param getter The function on <code>ENTITY</code> that returns the data element.
     * @param setter The function on <code>ENTITY</code> that consumes the data element.
     * @param daoDescriptor The description of how the mapping for the subordinate element
     *                      is persisted. Both <code>Dao</code> and <code>DaoBuilder</code>
     *                      objects implement the <code>DaoDescriptor</code> interface.
     * @param <U> The type of the data element.
     * @return This instance.
     */
    public <U> KeylessDaoBuilder<ENTITY, BUILDER> withJoinColumn(String columnName, Function<ENTITY, U> getter, BiConsumer<BUILDER,U> setter, DaoDescriptor<U,?> daoDescriptor){
        internalDaoBuilder.withJoinColumn(columnName, getter, setter, daoDescriptor);
        return this;
    }

    /**
     * Describes a relationship between the object <code>ENTITY</code> and its several
     * child objects of type <code>U</code>.
     *
     * <p>
     * When hrorm inserts or updates objects with children it will attempt to
     * create, update, or delete child elements as necessary.</p>
     *
     * <p>The above should be emphasized. For the purposes of persistence, Hrorm
     * treats child objects (and grandchild and further generations of objects
     * transitively) as wholly owned by the parent object. On an update or
     * delete of the parent, the child objects will be updated or deleted as
     * necessary. Imagine a schema with a recipe entity and an ingredient
     * entity. The ingredient entities are children of various recipes. If
     * the recipe for bechamel is deleted, it makes no sense to have an
     * orphaned ingredient entry for one cup of butter. It will therefore be
     * deleted.</p>
     *
     * <p>Contrast this behavior with the join column functionality, which describes
     * the situation wherein the object makes no sense without the joined relation.</p>
     *
     * @param getter The function on <code>ENTITY</code> that returns the children.
     * @param setter The function on <code>ENTITY</code> that consumes the children.
     * @param daoDescriptor The description of how the mapping for the subordinate elements
     *                      are persisted. Both <code>Dao</code> and <code>DaoBuilder</code>
     *                      objects implement the <code>DaoDescriptor</code> interface.
     * @param <U> The type of the child data elements.
     * @param <UB> The type of the builder of child data elements
     * @return This instance.
     */
    public <U,UB> KeylessDaoBuilder<ENTITY, BUILDER> withChildren(Function<ENTITY, List<U>> getter, BiConsumer<BUILDER, List<U>> setter, DaoDescriptor<U,UB> daoDescriptor){
        internalDaoBuilder.withChildren(getter, setter, daoDescriptor);
        return this;
    }

    /**
     * Indicator that the column is a reference to an owning parent object.
     *
     * @param columnName The name of the column that holds the foreign key reference.
     * @param getter The function to call for setting the parent onto the child.
     * @param setter The function to call for getting the parent from the child.
     * @param <P> The type of the parent object.
     * @return This instance.
     */
    public <P> KeylessDaoBuilder<ENTITY, BUILDER> withParentColumn(String columnName, Function<ENTITY,P> getter, BiConsumer<BUILDER,P> setter){
        internalDaoBuilder.withParentColumn(columnName, getter, setter);
        return this;
    }

    /**
     * Indicator that the column is a reference to an owning parent object.
     *
     * @param columnName The name of the column that holds the foreign key reference.
     * @param <P> The type of the parent object.
     * @return This instance.
     */
    public <P> KeylessDaoBuilder<ENTITY, BUILDER> withParentColumn(String columnName){
        internalDaoBuilder.withParentColumn(columnName);
        return this;
    }


    /**
     * Sets the most recent column added to this DaoBuilder to prevent it allowing
     * nulls on inserts or updates.
     *
     * @return This instance.
     */
    public KeylessDaoBuilder<ENTITY, BUILDER> notNull(){
        internalDaoBuilder.notNull();
        return this;
    }

}
