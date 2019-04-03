package org.hrorm.jdbc.types;

import java.sql.Types;
import java.util.Set;

public class DoubleType extends ColumnType<Double> {

    public static DoubleType DECIMAL(String sqlTypeName) { return new DoubleType(Types.DECIMAL, sqlTypeName); }
    public static DoubleType DOUBLE(String sqlTypeName) { return new DoubleType(Types.DOUBLE, sqlTypeName); }
    public static DoubleType FLOAT(String sqlTypeName) { return new DoubleType(Types.FLOAT, sqlTypeName); }
    public static DoubleType REAL(String sqlTypeName) { return new DoubleType(Types.REAL, sqlTypeName); }
    public static DoubleType NUMERIC(String sqlTypeName) { return new DoubleType(Types.NUMERIC, sqlTypeName); }

    private DoubleType(int sqlType, String sqlTypeName) {
        super(Double.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.DecimalTypes;
    }
}
