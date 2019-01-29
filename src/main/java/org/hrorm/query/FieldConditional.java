package org.hrorm.query;

public class FieldConditional<ENTITY, FIELDTYPE> extends Conditional<ENTITY> {
    private final Field<ENTITY, FIELDTYPE> field;
    private final ComparisonOperator comparisonOperator;
    private final FIELDTYPE value;

    public FieldConditional(Field<ENTITY, FIELDTYPE> field, ComparisonOperator comparisonOperator, FIELDTYPE value) {
        this.field = field;
        this.comparisonOperator = comparisonOperator;
        this.value = value;
    }

    public Field<ENTITY, FIELDTYPE> getField() {
        return field;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public FIELDTYPE getValue() {
        return value;
    }
}
