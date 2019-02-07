package org.hrorm.query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Compound<ENTITY> extends Conditional<ENTITY> {
    private final List<Conditional<ENTITY>> conditionals = new ArrayList<>();
    private final FunctionalOperator functionalOperator;

    public Compound(FunctionalOperator functionalOperator) {
        this.functionalOperator = functionalOperator;
    }

    private Compound<ENTITY> appendOrNest(FunctionalOperator functionalOperator, Conditional<ENTITY> conditional) {
        if (functionalOperator == this.functionalOperator) {
            conditionals.add(conditional);
            return this;
        }
        Compound<ENTITY> nested = new Compound<ENTITY>(functionalOperator);
        nested.append(this);
        nested.append(conditional);
        return nested;
    }

    public Compound<ENTITY> and(Conditional<ENTITY> conditional) {
        return appendOrNest(FunctionalOperator.AND, conditional);
    }

    public Compound<ENTITY> or(Conditional<ENTITY> conditional) {
        return appendOrNest(FunctionalOperator.OR, conditional);
    }

    public <FIELDTYPE> Field<ENTITY, FIELDTYPE> field(String columnName, Function<ENTITY, FIELDTYPE> getter, BiConsumer<ENTITY, FIELDTYPE> setter) {
        return new Field<>(columnName, getter, setter);
    }

    public <FIELDTYPE> Compound<ENTITY> append(Conditional<ENTITY> conditional) {
        conditionals.add(conditional);
        return this;
    }

    @Override
    public String sqlFragment() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(conditionals.stream()
                .map(Conditional::sqlFragment)
                .collect(Collectors.joining(functionalOperator.name())));
        sb.append(")");
        return sb.toString();
    }

    @Override
    public List<Field<ENTITY, ?>> fieldPositions() {
        return conditionals.stream()
                .map(Conditional::fieldPositions)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
