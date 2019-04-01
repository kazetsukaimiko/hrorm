package org.hrorm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class generates SQL strings suitable to be used in
 * {@link java.sql.PreparedStatement}s used by {@link KeylessDao}s.
 *
 * <p>
 *
 * Most users of hrorm will have no need to directly use this.
 *
 * @param <ENTITY> The type of the entity being persisted.
 */
public class KeylessSqlBuilder<ENTITY> {

    private final String table;
    private final List<? extends Column<ENTITY,?>> nonJoinColumns;
    private final List<? extends JoinColumn<ENTITY, ?, ?, ?>> joinColumns;

    public KeylessSqlBuilder(KeylessDaoDescriptor<ENTITY,?> daoDescriptor){
        this.table = daoDescriptor.tableName();
        this.nonJoinColumns = daoDescriptor.nonJoinColumns();
        this.joinColumns = daoDescriptor.joinColumns();
    }

    public String getTable(){
        return table;
    }

    public List<? extends Column<ENTITY, ?>> getNonJoinColumns() {
        return nonJoinColumns;
    }

    public List<? extends JoinColumn<ENTITY, ?, ?, ?>> getJoinColumns() {
        return joinColumns;
    }

    private String columnsAsString(String prefix, boolean withAliases, List<? extends Column> columns){
        Function<Column,String> stringer;
        if( withAliases && prefix != null ) {
            stringer = c -> prefix + "." + c.getName() + " as " + prefix + c.getName();
        } else if (prefix != null ){
            stringer = c -> prefix + c.getName();
        } else {
            stringer = Column::getName;
        }
        List<String> columnNames = columns.stream().map(stringer).collect(Collectors.toList());
        return String.join(", ", columnNames);
    }

    public String select(){
        StringBuilder buf = new StringBuilder();
        buf.append("select ");
        buf.append(columnsAsString("a", true, nonJoinColumns));
        for(JoinColumn<?, ?, ?, ?> joinColumn : flattenedJoinColumns()) {
            buf.append(", ");
            buf.append(columnsAsString(
                    joinColumn.getPrefix(),
                    true,
                    joinColumn.getNonJoinColumns()
            ));
        }
        buf.append(" from ");
        buf.append(table);
        buf.append(" a");
        List<JoinColumn> flattenedJoinColumns = flattenedJoinColumns();
        for(JoinColumn joinColumn : flattenedJoinColumns) {
            buf.append(" LEFT JOIN ");
            buf.append(joinColumn.getTable());
            buf.append(" ");
            buf.append(joinColumn.getPrefix());
            buf.append(" ON ");
            buf.append(joinColumn.getJoinedTablePrefix());
            buf.append(".");
            buf.append(joinColumn.getName());
            buf.append("=");
            buf.append(joinColumn.getPrefix());
            buf.append(".");
            buf.append(joinColumn.getJoinedTablePrimaryKeyName());
        }
        buf.append(" where 1=1 ");

        return buf.toString();
    }

    public String select(Order order){
        return select() + order.render();
    }

    public String select(Where where){
        String whereClause = where.render();
        if ( whereClause.length() > 0 ) {
            return select() + " AND " + whereClause;
        } else {
            return select();
        }
    }

    public String select(Where where, Order order){
        String sql = select(where);
        return sql + order.render();
    }

    public String selectFunction(SqlFunction function, String columnName, Where where){
        StringBuilder buf = new StringBuilder();
        buf.append("select ");
        buf.append(function.getFunctionName());
        buf.append(" ( ");
        buf.append(columnName);
        buf.append(" ) ");
        buf.append(" from ");
        buf.append(table);
        buf.append(" a");

        String whereClause = where.render();
        if ( whereClause.length() > 1) {
            buf.append(" where ");
            buf.append(whereClause);
        }

        return buf.toString();
    }

    private List<JoinColumn> flattenedJoinColumns(){
        List<JoinColumn> flatJoinColumnList = new ArrayList<>();
        for(JoinColumn joinColumn : joinColumns){
            prependColumnsRecursively(flatJoinColumnList, joinColumn);
        }
        return flatJoinColumnList;
    }

    private void prependColumnsRecursively(List<JoinColumn> listToBuild, JoinColumn columnToAdd){
        List<JoinColumn> listToAppend = columnToAdd.getTransitiveJoins();
        listToAppend.forEach(c -> prependColumnsRecursively(listToBuild, c));
        listToBuild.add(0, columnToAdd);
    }

    public String selectByColumns(ColumnSelection selectColumnList){
        StringBuilder buf = new StringBuilder();
        buf.append(select());
        buf.append(selectColumnList.whereClause());
        return buf.toString();
    }

    public String selectByColumns(ColumnSelection columnSelection, Order order){
        return selectByColumns(columnSelection) + order.render();
    }

    public String insert(){
        StringBuilder bldr = new StringBuilder();
        bldr.append("insert into ");
        bldr.append(table);
        bldr.append(" ( ");
        bldr.append(columnsAsString("", false, nonJoinColumns));
        if( ! joinColumns.isEmpty() ) {
            bldr.append(", ");
            bldr.append(columnsAsString("", false, joinColumns));
        }
        bldr.append(" ) values ( ");
        int end = nonJoinColumns.size() - 1;
        if (!joinColumns.isEmpty()){
            end += joinColumns.size();
        }
        for(int idx=0; idx<end; idx++){
            bldr.append("?, ");
        }
        bldr.append("? ");
        bldr.append(" ) ");

        return bldr.toString();
    }
}
