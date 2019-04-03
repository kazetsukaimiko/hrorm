package org.hrorm.jdbc.types;

import java.sql.Types;
import java.util.Set;

public class FloatType extends ColumnType<Float> {

    public static final FloatType DECIMAL = new FloatType(Types.DECIMAL);
    public static final FloatType DOUBLE = new FloatType(Types.DOUBLE);
    public static final FloatType FLOAT = new FloatType(Types.FLOAT);
    public static final FloatType REAL = new FloatType(Types.REAL);
    public static final FloatType NUMERIC = new FloatType(Types.NUMERIC);

    protected FloatType(int sqlType) {
        this(sqlType, null);    }

    protected FloatType(int sqlType, String sqlTypeName) {
        super(Float.class, sqlType, sqlTypeName);
    }


    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.DecimalTypes;
    }
}
