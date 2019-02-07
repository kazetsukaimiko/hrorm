package org.hrorm.query;

import org.hrorm.Operator;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class Field<ENTITY, FIELDTYPE> {
    private final String columnName;
    private final Function<ENTITY, FIELDTYPE> getter;
    private final BiConsumer<ENTITY, FIELDTYPE> setter;

    public Field(String columnName, Function<ENTITY, FIELDTYPE> getter, BiConsumer<ENTITY, FIELDTYPE> setter) {
        this.columnName = columnName;
        this.getter = getter;
        this.setter = setter;
    }

    public String getColumnName() {
        return columnName;
    }

    public Function<ENTITY, FIELDTYPE> getGetter() {
        return getter;
    }

    public FieldConditional<ENTITY, FIELDTYPE> equalTo(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.EQUALS, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> notEqualTo(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.NOT_EQUALS, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> greaterThan(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.GREATER_THAN, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> lessThan(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.LESS_THAN, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> isNull() {
        return new FieldConditional<>(this, Operator.EQUALS, null);
    }

}
