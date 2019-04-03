package org.hrorm.jdbc.types;

public class ColumnType<TYPE> {
    private final Class<TYPE> jdbcType;
    private final int sqlType;
    private final String sqlTypeName;

    private static String defaultSqlTypeName(int sqlType) {
        return null;
    }

    protected ColumnType(Class<TYPE> jdbcType, int sqlType) {
        this(jdbcType, sqlType, defaultSqlTypeName(sqlType));

    }

    protected ColumnType(Class<TYPE> jdbcType, int sqlType, String sqlTypeName) {
        this.jdbcType = jdbcType;
        this.sqlType = sqlType;
        this.sqlTypeName = sqlTypeName;
    }
}
