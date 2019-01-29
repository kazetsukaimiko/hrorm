package org.hrorm.query;

import java.util.stream.Stream;

public class FieldMultiConditional<ENTITY, FIELDTYPE> extends Conditional<ENTITY> {
    private final Field<ENTITY, FIELDTYPE> field;
    private final MultiComparisonOperator multiComparisonOperator;
    private final Stream<FIELDTYPE> valueStream;

    public FieldMultiConditional(Field<ENTITY, FIELDTYPE> field, MultiComparisonOperator multiComparisonOperator, Stream<FIELDTYPE> valueStream) {
        this.field = field;
        this.multiComparisonOperator = multiComparisonOperator;
        this.valueStream = valueStream;
    }

    public Field<ENTITY, FIELDTYPE> getField() {
        return field;
    }

    public MultiComparisonOperator getMultiComparisonOperator() {
        return multiComparisonOperator;
    }

    public Stream<FIELDTYPE> getValueStream() {
        return valueStream;
    }
}
