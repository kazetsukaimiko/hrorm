package org.hrorm.jdbc.types;


import java.sql.Types;
import java.util.Set;

public class IntegerType extends ColumnType<Integer> {
    public static IntegerType INTEGER(String sqlTypeName) { return new IntegerType(Types.INTEGER, sqlTypeName); }
    public static IntegerType BIGINT(String sqlTypeName) { return new IntegerType(Types.BIGINT, sqlTypeName); }
    public static IntegerType SMALLINT(String sqlTypeName) { return new IntegerType(Types.SMALLINT, sqlTypeName); }

    protected IntegerType(int sqlType, String sqlTypeName) {
        super(Integer.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.IntegerTypes;
    }
}
