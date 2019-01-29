package org.hrorm.query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Compound<ENTITY> extends Conditional<ENTITY> {
    private final Class<ENTITY> entityClass;
    private final List<Conditional<ENTITY>> conditionals = new ArrayList<>();
    private final FunctionalOperator functionalOperator;
    private final Compound<ENTITY> parent;

    public Compound(Class<ENTITY> entityClass, FunctionalOperator functionalOperator, Compound<ENTITY> parent) {
        this.entityClass = entityClass;
        this.functionalOperator = functionalOperator;
        this.parent = parent;
    }

    public Compound(Class<ENTITY> entityClass, FunctionalOperator functionalOperator) {
        this(entityClass, functionalOperator, null);
    }

    public Compound(Class<ENTITY> entityClass) {
        this(entityClass, FunctionalOperator.AND);
    }

    public Compound<ENTITY> and() {
        if (functionalOperator == FunctionalOperator.AND) {
            return this;
        }
        Compound<ENTITY> andCompound = new Compound<>(entityClass, FunctionalOperator.AND, this);
        conditionals.add(andCompound);
        return andCompound;
    }

    public Compound<ENTITY> or() {
        if (functionalOperator == FunctionalOperator.OR) {
            return this;
        }
        Compound<ENTITY> orCompound = new Compound<>(entityClass, FunctionalOperator.OR, this);
        conditionals.add(orCompound);
        return orCompound;
    }

    public <FIELDTYPE> Field<ENTITY, FIELDTYPE> field(String columnName, Function<ENTITY, FIELDTYPE> getter) {
        return new Field<>(columnName, getter, this);
    }

    public boolean hasParent() {
        return up() != null;
    }

    public Compound<ENTITY> up() {
        return parent;
    }

    public <FIELDTYPE> Compound<ENTITY> append(FieldConditional<ENTITY, FIELDTYPE> fieldConditional) {
        conditionals.add(fieldConditional);
        return this;
    }
}
