package org.hrorm.jdbc.types;

import java.sql.Types;
import java.util.Set;

public class BooleanType extends ColumnType<Boolean> {

    public static BooleanType INTEGER(String sqlTypeName) { return new BooleanType(Types.INTEGER, sqlTypeName); }
    public static BooleanType BIGINT(String sqlTypeName) { return new BooleanType(Types.BIGINT, sqlTypeName); }
    public static BooleanType SMALLINT(String sqlTypeName) { return new BooleanType(Types.SMALLINT, sqlTypeName); }
    public static BooleanType BIT(String sqlTypeName) { return new BooleanType(Types.BIT, sqlTypeName); }
    public static BooleanType BOOLEAN(String sqlTypeName) { return new BooleanType(Types.BOOLEAN, sqlTypeName); }
    public static BooleanType NUMERIC(String sqlTypeName) { return new BooleanType(Types.NUMERIC, sqlTypeName); }

    private BooleanType(int sqlType, String sqlTypeName) {
        super(Boolean.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.BooleanTypes;
    }
}
