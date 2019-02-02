package org.hrorm;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@link KeylessDao} implementation.
 *
 * <p>
 *
 * There is no good reason to directly construct this class yourself.
 * Use a {@link IndirectKeylessDaoBuilder} or {@link IndirectDaoBuilder}.
 *
 * @param <ENTITY> The type whose persistence is managed by this <code>Dao</code>.
 * @param <PARENT> The type of the parent (if any) of type <code>ENTITY</code>.
 * @param <BUILDER> The type of object that can build an <code>ENTITY</code> instance.
 * @param <PARENTBUILDER> The type of the object that can build a <code>PARENT</code> instance.
 */
public class KeylessDaoImpl<ENTITY, PARENT, BUILDER, PARENTBUILDER> implements KeylessDao<ENTITY>, KeylessDaoDescriptor<ENTITY, BUILDER> {

    protected final Connection connection;
    protected final String tableName;
    private final List<Column<ENTITY, BUILDER>> dataColumns;
    protected final Supplier<BUILDER> supplier;
    private final List<JoinColumn<ENTITY,?, BUILDER,?>> joinColumns;
    protected final List<ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors;
    protected final KeylessSqlBuilder<ENTITY> keylessSqlBuilder;
    protected final SqlRunner<ENTITY, BUILDER> sqlRunner;
    protected final ParentColumn<ENTITY, PARENT, BUILDER, PARENTBUILDER> parentColumn;
    protected final Function<BUILDER, ENTITY> buildFunction;

    public KeylessDaoImpl(Connection connection,
                          DaoDescriptor<ENTITY, BUILDER> daoDescriptor){
        this.connection = connection;
        this.tableName = daoDescriptor.tableName();
        this.dataColumns = Collections.unmodifiableList(new ArrayList<>(daoDescriptor.dataColumns()));
        this.supplier = daoDescriptor.supplier();
        this.joinColumns = Collections.unmodifiableList(new ArrayList<>(daoDescriptor.joinColumns()));
        this.childrenDescriptors = Collections.unmodifiableList(new ArrayList<>(daoDescriptor.childrenDescriptors()));
        this.parentColumn = daoDescriptor.parentColumn();
        this.buildFunction = daoDescriptor.buildFunction();

        this.keylessSqlBuilder = new KeylessSqlBuilder<>(tableName, this.dataColumnsWithParent(), this.joinColumns);
        this.sqlRunner = new SqlRunner<>(connection, this);
    }

    @Override
    public String tableName(){
        return tableName;
    }

    @Override
    public List<Column<ENTITY, BUILDER>> dataColumns(){
        return dataColumns;
    }

    @Override
    public List<JoinColumn<ENTITY, ?, BUILDER, ?>> joinColumns(){
        return joinColumns;
    }

    @Override
    public Supplier<BUILDER> supplier() { return supplier; }

    @Override
    public List<ChildrenDescriptor<ENTITY, ?, BUILDER, ?>> childrenDescriptors() {
        return null;
    }

    @Override
    public ParentColumn<ENTITY, PARENT, BUILDER, PARENTBUILDER> parentColumn() {
        return parentColumn;
    }

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
        if( parentColumn != null ){
            Long parentId = parentColumn.getParentId(item);
            if ( parentId != null ){
                return new Envelope<>(item, id, parentId);
            }
        }
        return new Envelope<>(item, id);
    }


    protected Stream<ENTITY> mapBuilders(Stream<BUILDER> bs){
        return bs.map(buildFunction);
    }


    @Override
    public Stream<ENTITY> streamAll() {
        String sql = keylessSqlBuilder.select();
        Stream<BUILDER> bs = sqlRunner.select(sql, supplier, childrenDescriptors);
        return mapBuilders(bs);
    }

    @Override
    public ENTITY selectByColumns(ENTITY item, String ... columnNames){
        List<ENTITY> items = selectManyByColumns(item, columnNames);
        return fromSingletonList(items);
    }

    @Override
    public Stream<ENTITY> streamManyByColumns(ENTITY item, String ... columnNames) {
        String sql = keylessSqlBuilder.selectByColumns(Collections.emptyMap(), columnNames);
        Stream<BUILDER> bs = sqlRunner.selectByColumns(sql, supplier, Arrays.asList(columnNames), columnMap(columnNames), childrenDescriptors, item);
        return mapBuilders(bs);
    }

    @Override
    public Stream<ENTITY> streamManyByColumns(ENTITY template, Map<String, Operator> columnNamesMap) {
        String[] columnNames = columnNamesMap.keySet().stream().toArray(String[]::new);
        String sql = keylessSqlBuilder.selectByColumns(columnNamesMap, columnNames);
        Stream<BUILDER> bs = sqlRunner.selectByColumns(sql, supplier, Arrays.asList(columnNames), columnMap(columnNames), childrenDescriptors, template);
        return mapBuilders(bs);
    }

    @Override
    public <T> T foldingSelect(ENTITY item, T identity, BiFunction<T,ENTITY,T> accumulator, String ... columnNames){
        String sql = keylessSqlBuilder.selectByColumns(Collections.emptyMap(), columnNames);
        return sqlRunner.foldingSelect(sql, supplier, Arrays.asList(columnNames), columnMap(columnNames), childrenDescriptors, item)
            .map(buildFunction)
            .reduce(identity, accumulator, (a, b) -> a);
    }

    protected <A> A fromSingletonList(List<A> items) {
        if (items.isEmpty()) {
            return null;
        }
        if (items.size() == 1) {
            return items.get(0);
        }
        throw new HrormException("Found " + items.size() + " items.");
    }
}
