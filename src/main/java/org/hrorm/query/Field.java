package org.hrorm.query;

import java.util.function.Function;

public class Field<ENTITY, FIELDTYPE> {
    private final String columnName;
    private final Function<ENTITY, FIELDTYPE> getter;
    private final Compound<ENTITY> compound;

    public Field(String columnName, Function<ENTITY, FIELDTYPE> getter, Compound<ENTITY> compound) {
        this.columnName = columnName;
        this.getter = getter;
        this.compound = compound;
    }

    public String getColumnName() {
        return columnName;
    }

    public Function<ENTITY, FIELDTYPE> getGetter() {
        return getter;
    }

    public Compound<ENTITY> equalTo(FIELDTYPE value) {
        FieldConditional<ENTITY, FIELDTYPE> fieldConditional = new FieldConditional<>(this, ComparisonOperator.EQUALS, value);
        return compound.append(fieldConditional);
    }

    public Compound<ENTITY> greaterThan(FIELDTYPE value) {
        FieldConditional<ENTITY, FIELDTYPE> fieldConditional = new FieldConditional<>(this, ComparisonOperator.GREATER_THAN, value);
        return compound.append(fieldConditional);
    }

    public Compound<ENTITY> lessThan(FIELDTYPE value) {
        FieldConditional<ENTITY, FIELDTYPE> fieldConditional = new FieldConditional<>(this, ComparisonOperator.LESS_THAN, value);
        return compound.append(fieldConditional);
    }

    public Compound<ENTITY> isNull() {
        FieldConditional<ENTITY, FIELDTYPE> fieldConditional = new FieldConditional<>(this, ComparisonOperator.EQUALS, null);
        return compound.append(fieldConditional);
    }


}
