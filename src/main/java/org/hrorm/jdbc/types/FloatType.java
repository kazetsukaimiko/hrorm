package org.hrorm.jdbc.types;

import java.sql.Types;
import java.util.Set;

public class FloatType extends ColumnType<Float> {

    public static FloatType DECIMAL(String sqlTypeName) { return new FloatType(Types.DECIMAL, sqlTypeName); }
    public static FloatType DOUBLE(String sqlTypeName) { return new FloatType(Types.DOUBLE, sqlTypeName); }
    public static FloatType FLOAT(String sqlTypeName) { return new FloatType(Types.FLOAT, sqlTypeName); }
    public static FloatType REAL(String sqlTypeName) { return new FloatType(Types.REAL, sqlTypeName); }
    public static FloatType NUMERIC(String sqlTypeName) { return new FloatType(Types.NUMERIC, sqlTypeName); }

    private FloatType(int sqlType, String sqlTypeName) {
        super(Float.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.DecimalTypes;
    }
}
