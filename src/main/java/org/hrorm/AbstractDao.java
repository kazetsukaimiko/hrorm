package org.hrorm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An abstract class that is almost a <code>KeylessDao</code>.
 *
 * <p>
 *     Most users of hrorm will have no need to directly use this.
 * </p>
 *
 * @param <ENTITY> The type whose persistence is managed by this <code>Dao</code>.
 * @param <BUILDER> The type of object that can build an <code>ENTITY</code> instance.
 */
public abstract class AbstractDao<ENTITY, BUILDER> implements KeylessDaoDescriptor<ENTITY, BUILDER>, KeylessDao<ENTITY> {

    protected final Connection connection;

    protected final SqlBuilder<ENTITY> sqlBuilder;
    protected final SqlRunner<ENTITY, BUILDER> sqlRunner;

    private final String tableName;
    private final Supplier<BUILDER> supplier;
    private final Function<BUILDER, ENTITY> buildFunction;
    private final ColumnCollection<ENTITY, BUILDER> columnCollection;

    public AbstractDao(Connection connection,
                       KeylessDaoDescriptor<ENTITY, BUILDER> keylessDaoDescriptor){
        this.connection = connection;
        this.tableName = keylessDaoDescriptor.tableName();
        this.columnCollection = keylessDaoDescriptor.getColumnCollection();
        this.supplier = keylessDaoDescriptor.supplier();
        this.buildFunction = keylessDaoDescriptor.buildFunction();

        this.sqlBuilder = new SqlBuilder<>(keylessDaoDescriptor);
        this.sqlRunner = new SqlRunner<>(connection, keylessDaoDescriptor);
    }

    public AbstractDao(Connection connection,
                       DaoDescriptor<ENTITY, BUILDER> daoDescriptor){
        this.connection = connection;
        this.tableName = daoDescriptor.tableName();
        this.columnCollection = daoDescriptor.getColumnCollection();
        this.supplier = daoDescriptor.supplier();
        this.buildFunction = daoDescriptor.buildFunction();

        this.sqlBuilder = new SqlBuilder<>(daoDescriptor);
        this.sqlRunner = new SqlRunner<>(connection, daoDescriptor);
    }

    protected abstract List<ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors();

    public abstract Long insert(ENTITY item);

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public Supplier<BUILDER> supplier() {
        return supplier;
    }

    @Override
    public Function<BUILDER, ENTITY> buildFunction() {
        return buildFunction;
    }

    @Override
    public ColumnCollection<ENTITY, BUILDER> getColumnCollection() {
        return columnCollection;
    }

    @Override
    public Long atomicInsert(ENTITY item) {
        Transactor transactor = new Transactor(connection);
        return transactor.runAndCommit(
                con -> { return insert(item); }
        );
    }

    @Override
    public Stream<ENTITY> streamAll() {
        String sql = sqlBuilder.select();
        return sqlRunner.select(sql, supplier, childrenDescriptors())
                .map(buildFunction);
    }

    @Override
    public Stream<ENTITY> streamAll(Order order) {
        String sql = sqlBuilder.select(order);
        return sqlRunner.select(sql, supplier, childrenDescriptors())
                .map(buildFunction);
    }


    @Override
    public ENTITY selectByColumns(ENTITY item, String ... columnNames){
        List<ENTITY> items = selectManyByColumns(item, columnNames);
        return fromSingletonList(items);
    }


    @Override
    public Stream<ENTITY> streamManyByColumns(ENTITY item, String ... columnNames) {
        ColumnSelection columnSelection = select(columnNames);
        String sql = sqlBuilder.selectByColumns(columnSelection);
        return sqlRunner.selectByColumns(sql, supplier, select(columnNames), childrenDescriptors(), item)
                .map(buildFunction);
    }

    @Override
    public Stream<ENTITY> streamManyByColumns(ENTITY item, Order order, String ... columnNames) {
        ColumnSelection columnSelection = select(columnNames);
        String sql = sqlBuilder.selectByColumns(columnSelection, order);
        return sqlRunner.selectByColumns(sql, supplier, select(columnNames), childrenDescriptors(), item)
                .map(buildFunction);
    }

    @Override
    public Long runLongFunction(SqlFunction function,
                                String columnName,
                                Where where) {
        String sql = sqlBuilder.selectFunction(function, columnName, where);
        return sqlRunner.runLongFunction(sql, where);
    }

    @Override
    public BigDecimal runBigDecimalFunction(SqlFunction function,
                                            String columnName,
                                            Where where) {
        String sql = sqlBuilder.selectFunction(function, columnName, where);
        return sqlRunner.runBigDecimalFunction(sql, where);
    }


    @Override
    public <T> T foldingSelect(T identity, BiFunction<T,ENTITY,T> accumulator, Where where){
        String sql = sqlBuilder.select(where) ;
        return sqlRunner.foldingSelect(sql, where, supplier, childrenDescriptors(), buildFunction, identity, accumulator);
    }

    @Override
    public Stream<ENTITY> stream(Where where) {
        String sql = sqlBuilder.select(where);
        return sqlRunner.selectWhere(sql, supplier, childrenDescriptors(), where)
                .map(buildFunction);
    }

    @Override
    public Stream<ENTITY> stream(Where where, Order order) {
        String sql = sqlBuilder.select(where, order);
        return sqlRunner.selectWhere(sql, supplier, childrenDescriptors(), where)
                .map(buildFunction);
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
