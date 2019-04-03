package org.hrorm.jdbc.types;


import java.sql.Types;
import java.util.Set;

public class LongType extends ColumnType<Long> {
    public static LongType INTEGER(String sqlTypeName) { return new LongType(Types.INTEGER, sqlTypeName); }
    public static LongType BIGINT(String sqlTypeName) { return new LongType(Types.BIGINT, sqlTypeName); }
    public static LongType SMALLINT(String sqlTypeName) { return new LongType(Types.SMALLINT, sqlTypeName); }

    private LongType(int sqlType, String sqlTypeName) {
        super(Long.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.IntegerTypes;
    }
}
