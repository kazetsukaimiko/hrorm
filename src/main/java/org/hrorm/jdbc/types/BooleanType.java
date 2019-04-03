package org.hrorm.jdbc.types;

import java.sql.Types;

public class BooleanType extends ColumnType<Boolean> {

    public static final BooleanType INTEGER = new BooleanType(Types.INTEGER);
    public static final BooleanType BIGINT = new BooleanType(Types.BIGINT);
    public static final BooleanType SMALLINT = new BooleanType(Types.SMALLINT);
    public static final BooleanType BIT = new BooleanType(Types.BIT);
    public static final BooleanType BOOLEAN = new BooleanType(Types.BOOLEAN);
    public static final BooleanType NUMERIC = new BooleanType(Types.NUMERIC);

    public BooleanType(int sqlType) {
        this(sqlType, null);    }

    public BooleanType(int sqlType, String sqlTypeName) {
        super(Boolean.class, sqlType, sqlTypeName);
    }
}
