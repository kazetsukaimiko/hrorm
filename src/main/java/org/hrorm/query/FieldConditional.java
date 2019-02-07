package org.hrorm.query;

import org.hrorm.Operator;

import java.util.Collections;
import java.util.List;

public class FieldConditional<ENTITY, FIELDTYPE> extends Conditional<ENTITY> {
    private final Field<ENTITY, FIELDTYPE> field;
    private final Operator operator;
    private final FIELDTYPE value;

    public FieldConditional(Field<ENTITY, FIELDTYPE> field, Operator operator, FIELDTYPE value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public Field<ENTITY, FIELDTYPE> getField() {
        return field;
    }

    public Operator getOperator() {
        return operator;
    }

    public FIELDTYPE getValue() {
        return value;
    }

    @Override
    public String sqlFragment() {
        StringBuilder sb = new StringBuilder(field.getColumnName());
        sb.append(operator.getSqlString()); // Cannot have values in here- screws up order
        sb.append("?");
        return sb.toString();
    }

    @Override
    public List<Field<ENTITY, ?>> fieldPositions() {
        return Collections.singletonList(field);
    }

    public Compound<ENTITY> and(FieldConditional<ENTITY, ?> other) {
        return compound(FunctionalOperator.AND, other);
    }

    public Compound<ENTITY> or(FieldConditional<ENTITY, ?> other) {
        return compound(FunctionalOperator.OR, other);
    }

    private Compound<ENTITY> compound(FunctionalOperator functionalOperator, FieldConditional<ENTITY, ?> other) {
        Compound<ENTITY> compound = new Compound<>(functionalOperator);
        compound.append(this);
        compound.append(other);
        return compound;
    }
}
