package org.hrorm.jdbc.types;


import java.sql.Types;
import java.util.Set;

public class LongType extends ColumnType<Long> {
    public static final LongType INTEGER = new LongType(Types.INTEGER);
    public static final LongType BIGINT = new LongType(Types.BIGINT);
    public static final LongType SMALLINT = new LongType(Types.SMALLINT);

    protected LongType(int sqlType) {
        this(sqlType, null);    }

    protected LongType(int sqlType, String sqlTypeName) {
        super(Long.class, sqlType, sqlTypeName);
    }


    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.IntegerTypes;
    }
}
