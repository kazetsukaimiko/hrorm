package org.hrorm;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Representation of a SQL where clause: a possibly nested list of
 * predicates that describes which records in the database to match.
 */
public class Where implements Iterable<WherePredicate> {

    public static Where where(String columnName, Operator operator, Long value) {
        WherePredicate<Long> atom = WherePredicate.forLong(columnName, operator, value);
        return new Where(atom);
    }

    public static Where where(String columnName, Operator operator, String value){
        WherePredicate<String> atom = WherePredicate.forString(columnName, operator, value);
        return new Where(atom);
    }

    public static Where where(String columnName, Operator operator, BigDecimal value){
        WherePredicate<BigDecimal> atom = WherePredicate.forBigDecimal(columnName, operator, value);
        return new Where(atom);
    }

    public static Where where(Where subWhere){
        return new Where(subWhere);
    }

    private final WherePredicateTree tree;

    public Where(Where subWhere){
        WherePredicateTree.WherePredicateGroup group = new WherePredicateTree.WherePredicateGroup(subWhere.getRootNode());
        this.tree = new WherePredicateTree(group);
    }

    public Where(WherePredicate atom){
        tree = new WherePredicateTree(atom);
    }

    public Where and(String columnName, Operator operator, Long value){
        WherePredicate<Long> atom = WherePredicate.forLong(columnName, operator, value);
        tree.addAtom(WherePredicateTree.Conjunction.AND, atom);
        return this;
    }

    public Where and(String columnName, Operator operator, BigDecimal value){
        WherePredicate<BigDecimal> atom = WherePredicate.forBigDecimal(columnName, operator, value);
        tree.addAtom(WherePredicateTree.Conjunction.AND, atom);
        return this;
    }

    public Where and(String columnName, Operator operator, String value){
        WherePredicate<String> atom = WherePredicate.forString(columnName, operator, value);
        tree.addAtom(WherePredicateTree.Conjunction.AND, atom);
        return this;
    }


    public Where or(String columnName, Operator operator, Long value){
        WherePredicate<Long> atom = WherePredicate.forLong(columnName, operator, value);
        tree.addAtom(WherePredicateTree.Conjunction.OR, atom);
        return this;
    }

    public Where or(String columnName, Operator operator, BigDecimal value){
        WherePredicate<BigDecimal> atom = WherePredicate.forBigDecimal(columnName, operator, value);
        tree.addAtom(WherePredicateTree.Conjunction.OR, atom);
        return this;
    }

    public Where or(String columnName, Operator operator, String value){
        WherePredicate<String> atom = WherePredicate.forString(columnName, operator, value);
        tree.addAtom(WherePredicateTree.Conjunction.OR, atom);
        return this;
    }

    public Where and(Where where){
        tree.addNode(WherePredicateTree.Conjunction.AND, where.getRootNode());
        return this;
    }

    public String render(){
        return tree.render("a.");
    }

    public WherePredicateTree.WherePredicateNode getRootNode(){
        return tree.getRootNode();
    }

    @Override
    public Iterator<WherePredicate> iterator() {
        return tree.asList().iterator();
    }
}
