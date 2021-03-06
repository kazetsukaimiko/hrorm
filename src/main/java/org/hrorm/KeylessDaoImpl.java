package org.hrorm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The {@link KeylessDao} implementation.
 *
 * <p>
 *
 * There is no good reason to directly construct this class yourself.
 * Use a {@link IndirectKeylessDaoBuilder}.
 *
 * @param <ENTITY> The type whose persistence is managed by this <code>Dao</code>.
 * @param <PARENT> The type of the parent (if any) of type <code>ENTITY</code>.
 * @param <BUILDER> The type of object that can build an <code>ENTITY</code> instance.
 * @param <PARENTBUILDER> The type of the object that can build a <code>PARENT</code> instance.
 */
public class KeylessDaoImpl<ENTITY, PARENT, BUILDER, PARENTBUILDER> implements KeylessDao<ENTITY>, KeylessDaoDescriptor<ENTITY, BUILDER> {

    protected final Connection connection;
    protected final KeylessSqlBuilder<ENTITY> keylessSqlBuilder;
    protected final SqlRunner<ENTITY, BUILDER> sqlRunner;

    private final ColumnCollection<ENTITY, BUILDER> columnCollection;

    private final String tableName;
    private final Supplier<BUILDER> supplier;
    private final Function<BUILDER, ENTITY> buildFunction;

    private final List<ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors;

    public KeylessDaoImpl(Connection connection,
                          DaoDescriptor<ENTITY, BUILDER> daoDescriptor){
        this(connection, daoDescriptor, daoDescriptor.childrenDescriptors());
    }

    public static KeylessDaoImpl forKeylessDescriptors(
            Connection connection,
            KeylessDaoDescriptor daoDescriptor){
        return new KeylessDaoImpl<>(connection, daoDescriptor, Collections.emptyList());
    }

    public KeylessDaoImpl(Connection connection,
                          KeylessDaoDescriptor<ENTITY, BUILDER> daoDescriptor,
                          List<ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors){
        this.connection = connection;
        this.tableName = daoDescriptor.tableName();
        this.columnCollection = daoDescriptor.getColumnCollection();
        this.supplier = daoDescriptor.supplier();
        this.buildFunction = daoDescriptor.buildFunction();

        this.keylessSqlBuilder = new KeylessSqlBuilder<>(this);
        this.sqlRunner = new SqlRunner<>(connection, daoDescriptor);
        this.childrenDescriptors = childrenDescriptors;
    }

    @Override
    public String tableName(){
        return tableName;
    }

    @Override
    public ColumnCollection<ENTITY, BUILDER> getColumnCollection() {
        return columnCollection;
    }

    @Override
    public Supplier<BUILDER> supplier() { return supplier; }

    @Override
    public Function<BUILDER, ENTITY> buildFunction() { return buildFunction; }

    @Override
    public Long atomicInsert(ENTITY item) {
        Transactor transactor = new Transactor(connection);
        return transactor.runAndCommit(
               con -> { return insert(item); }
        );
    }

    @Override
    public Long insert(ENTITY item) {
        String sql = keylessSqlBuilder.insert();
        Envelope<ENTITY> envelope = new Envelope(item);
        sqlRunner.insert(sql, envelope);
        for(ChildrenDescriptor<ENTITY,?, BUILDER,?> childrenDescriptor : childrenDescriptors){
            childrenDescriptor.saveChildren(connection, new Envelope<>(item));
        }
        return null;
    }

    protected Envelope<ENTITY> newEnvelope(ENTITY item, long id){
        return new Envelope<>(item, id);
    }


    protected List<ENTITY> mapBuilders(List<BUILDER> bs){
        return bs.stream().map(buildFunction).collect(Collectors.toList());
    }


    @Override
    public List<ENTITY> selectAll() {
        String sql = keylessSqlBuilder.select();
        List<BUILDER> bs = sqlRunner.select(sql, supplier, childrenDescriptors);
        return mapBuilders(bs);
    }

    @Override
    public List<ENTITY> selectAll(Order order) {
        String sql = keylessSqlBuilder.select(order);
        List<BUILDER> bs = sqlRunner.select(sql, supplier, childrenDescriptors);
        return mapBuilders(bs);
    }


    @Override
    public ENTITY selectByColumns(ENTITY item, String ... columnNames){
        List<ENTITY> items = selectManyByColumns(item, columnNames);
        return fromSingletonList(items);
    }

    @Override
    public List<ENTITY> selectManyByColumns(ENTITY item, String ... columnNames) {
        ColumnSelection columnSelection = select(columnNames);
        String sql = keylessSqlBuilder.selectByColumns(columnSelection);
        List<BUILDER> bs = sqlRunner.selectByColumns(sql, supplier, select(columnNames), childrenDescriptors, item);
        return mapBuilders(bs);
    }

    @Override
    public List<ENTITY> selectManyByColumns(ENTITY template, Order order, String... columnNames) {
        ColumnSelection columnSelection = select(columnNames);
        String sql = keylessSqlBuilder.selectByColumns(columnSelection, order);
        List<BUILDER> bs = sqlRunner.selectByColumns(sql, supplier, select(columnNames), childrenDescriptors, template);
        return mapBuilders(bs);
    }

    @Override
    public Long runLongFunction(SqlFunction function,
                                String columnName,
                                Where where) {
        String sql = keylessSqlBuilder.selectFunction(function, columnName, where);
        return sqlRunner.runLongFunction(sql, where);
    }

    @Override
    public BigDecimal runBigDecimalFunction(SqlFunction function,
                                            String columnName,
                                            Where where) {
        String sql = keylessSqlBuilder.selectFunction(function, columnName, where);
        return sqlRunner.runBigDecimalFunction(sql, where);
    }


    @Override
    public <T> T foldingSelect(T identity, BiFunction<T,ENTITY,T> accumulator, Where where){
        String sql = keylessSqlBuilder.select(where) ;
        return sqlRunner.foldingSelect(sql, where, supplier, childrenDescriptors, buildFunction, identity, accumulator);
    }

    @Override
    public List<ENTITY> select(Where where) {
        String sql = keylessSqlBuilder.select(where);
        List<BUILDER> bs = sqlRunner.selectWhere(sql, supplier, childrenDescriptors, where);
        return mapBuilders(bs);
    }

    @Override
    public List<ENTITY> select(Where where, Order order) {
        String sql = keylessSqlBuilder.select(where, order);
        List<BUILDER> bs = sqlRunner.selectWhere(sql, supplier, childrenDescriptors, where);
        return mapBuilders(bs);
    }

    public static <A> A fromSingletonList(List<A> items) {
        if (items.isEmpty()) {
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        throw new HrormException("Found " + items.size() + " items.");
    }
}
