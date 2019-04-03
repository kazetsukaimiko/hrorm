package org.hrorm.jdbc.types;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Set;

public class BigDecimalType extends ColumnType<BigDecimal> {

    public static final BigDecimalType DECIMAL = new BigDecimalType(Types.DECIMAL);
    public static final BigDecimalType DOUBLE = new BigDecimalType(Types.DOUBLE);
    public static final BigDecimalType FLOAT = new BigDecimalType(Types.FLOAT);
    public static final BigDecimalType REAL = new BigDecimalType(Types.REAL);
    public static final BigDecimalType NUMERIC = new BigDecimalType(Types.NUMERIC);

    public BigDecimalType(int sqlType) {
        this(sqlType, null);    }

    public BigDecimalType(int sqlType, String sqlTypeName) {
        super(BigDecimal.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.DecimalTypes;
    }
}
