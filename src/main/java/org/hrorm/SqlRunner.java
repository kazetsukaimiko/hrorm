package org.hrorm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * This class does the heavy lifting of creating <code>Statement</code>s,
 * executing SQL, and parsing <code>ResultSet</code>s.
 *
 * <p>
 *
 * Most users of hrorm will have no need to directly use this.
 *
 * @param <ENTITY> the type of object this runner supports
 * @param <BUILDER> the type of object that can construct new <code>ENTITY</code> instances
 */
public class SqlRunner<ENTITY, BUILDER> {

    private static final Logger logger = Logger.getLogger("org.hrorm");

    private final Connection connection;
    private final List<Column<ENTITY, BUILDER>> allColumns;

    public SqlRunner(Connection connection, DaoDescriptor<ENTITY, BUILDER> daoDescriptor) {
        this.connection = connection;
        List<Column<ENTITY, BUILDER>> columns = new ArrayList<>();
        columns.addAll(daoDescriptor.dataColumnsWithParent());
        columns.addAll(daoDescriptor.joinColumns());
        this.allColumns = Collections.unmodifiableList(columns);
    }

    public List<BUILDER> select(String sql, Supplier<BUILDER> supplier, List<ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors){
        return selectByColumns(sql, supplier, Collections.emptyList(), Collections.emptyMap(), childrenDescriptors, null);
    }

    public List<BUILDER> selectByColumns(String sql, Supplier<BUILDER> supplier, List<String> columnNames, Map<String, ? extends Column<ENTITY,?>> columnNameMap, List<? extends ChildrenDescriptor<ENTITY,?, BUILDER,?>> childrenDescriptors, ENTITY item){
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            int idx = 1;
            for(String columnName : columnNames){

                Column<ENTITY,?> column = columnNameMap.get(columnName.toUpperCase());
                column.setValue(item, idx, statement);
                idx++;
            }

            logger.info(sql);
            resultSet = statement.executeQuery();

            List<BUILDER> results = new ArrayList<>();

            while (resultSet.next()) {
                BUILDER result = populate(resultSet, supplier);
                for(ChildrenDescriptor<ENTITY,?, BUILDER,?> descriptor : childrenDescriptors){
                    descriptor.populateChildren(connection, result);
                }
                results.add(result);
            }

            return results;

        } catch (SQLException ex){
            throw new HrormException(ex, sql);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException se){
                throw new HrormException(se);
            }
        }
    }

    public void insert(String sql, Envelope<ENTITY> envelope) {
        runInsertOrUpdate(sql, envelope, false);
    }

    public void update(String sql, Envelope<ENTITY> envelope) {
        runInsertOrUpdate(sql, envelope, true);
    }

    private void runInsertOrUpdate(String sql, Envelope<ENTITY> envelope, boolean isUpdate){

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);

            int idx = 1;
            for(Column<ENTITY, BUILDER> column : allColumns){
                if( column.isPrimaryKey() ) {
                    if ( ! isUpdate ) {
                        preparedStatement.setLong(idx, envelope.getId());
                        idx++;
                    }
                } else if ( column.isParentColumn() ){
                    preparedStatement.setLong(idx, envelope.getParentId());
                    idx++;
                } else if ( ! column.isPrimaryKey()  ){
                    column.setValue(envelope.getItem(), idx, preparedStatement);
                    idx++;
                }
            }
            if( isUpdate ){
                preparedStatement.setLong(idx, envelope.getId());
            }

            logger.info(sql);
            preparedStatement.execute();

        } catch (SQLException se){
            throw new HrormException(se, sql);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se){
                throw new HrormException(se);
            }
        }

    }

    private BUILDER populate(ResultSet resultSet, Supplier<BUILDER> supplier)
            throws SQLException {
        BUILDER item = supplier.get();

        for (Column<ENTITY, BUILDER> column: allColumns) {
            PopulateResult populateResult = column.populate(item, resultSet);
            populateResult.populateChildren(connection);
        }

        return item;
    }
}
