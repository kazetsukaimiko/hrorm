package org.hrorm.query;

import org.hrorm.Operator;

import java.time.temporal.TemporalAccessor;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TemporalField<ENTITY, FIELDTYPE extends TemporalAccessor> extends Field<ENTITY, FIELDTYPE> {
    public TemporalField(String columnName, Function<ENTITY, FIELDTYPE> getter, BiConsumer<ENTITY, FIELDTYPE> setter) {
        super(columnName, getter, setter);
    }
    // Conversion: Timestamp.from(Instant.from(value))

    public FieldConditional<ENTITY, FIELDTYPE> isBefore(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.LESS_THAN, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> isAfter(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.GREATER_THAN, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> isAtOrBefore(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.LESS_THAN_OR_EQUALS, value);
    }

    public FieldConditional<ENTITY, FIELDTYPE> isAtOrAfter(FIELDTYPE value) {
        return new FieldConditional<>(this, Operator.GREATER_THAN_OR_EQUALS, value);
    }
}
