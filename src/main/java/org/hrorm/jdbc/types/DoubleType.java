package org.hrorm.jdbc.types;

import java.sql.Types;
import java.util.Set;

public class DoubleType extends ColumnType<Double> {

    public static final DoubleType DECIMAL = new DoubleType(Types.DECIMAL);
    public static final DoubleType DOUBLE = new DoubleType(Types.DOUBLE);
    public static final DoubleType FLOAT = new DoubleType(Types.FLOAT);
    public static final DoubleType REAL = new DoubleType(Types.REAL);
    public static final DoubleType NUMERIC = new DoubleType(Types.NUMERIC);

    protected DoubleType(int sqlType) {
        this(sqlType, null);    }

    protected DoubleType(int sqlType, String sqlTypeName) {
        super(Double.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.DecimalTypes;
    }
}
