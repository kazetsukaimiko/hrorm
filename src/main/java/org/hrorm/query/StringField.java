package org.hrorm.query;

import org.hrorm.Operator;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class StringField<ENTITY, FIELDTYPE> extends Field<ENTITY, FIELDTYPE> {
    public StringField(String columnName, Function<ENTITY, FIELDTYPE> getter, BiConsumer<ENTITY, FIELDTYPE> setter) {
        super(columnName, getter, setter);
    }


    public FieldConditional<ENTITY, FIELDTYPE> like(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.LIKE, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> notLike(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.NOT_LIKE, value);
    }
}
