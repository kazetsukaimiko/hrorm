package org.hrorm.jdbc.types;

import java.util.Set;

public abstract class ColumnType<TYPE> {
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

        if (sqlTypeName == null) {
            this.sqlTypeName = "UNSET";
        } else {
            this.sqlTypeName = sqlTypeName;
        }
    }

    public abstract Set<Integer> supportedTypes();

    public Class<TYPE> getJdbcType() {
        return jdbcType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public String getSqlTypeName() {
        return sqlTypeName;
    }
}
