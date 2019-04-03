package org.hrorm.jdbc.types;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Set;

public class BigDecimalType extends ColumnType<BigDecimal> {

    public static BigDecimalType DECIMAL(String sqlTypeName) { return new BigDecimalType(Types.DECIMAL, sqlTypeName); }
    public static BigDecimalType DOUBLE(String sqlTypeName) { return new BigDecimalType(Types.DOUBLE, sqlTypeName); }
    public static BigDecimalType FLOAT(String sqlTypeName) { return new BigDecimalType(Types.FLOAT, sqlTypeName); }
    public static BigDecimalType REAL(String sqlTypeName) { return new BigDecimalType(Types.REAL, sqlTypeName); }
    public static BigDecimalType NUMERIC(String sqlTypeName) { return new BigDecimalType(Types.NUMERIC, sqlTypeName); }

    private BigDecimalType(int sqlType, String sqlTypeName) {
        super(BigDecimal.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.DecimalTypes;
    }
}
