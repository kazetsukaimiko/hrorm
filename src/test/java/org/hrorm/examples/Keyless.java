package org.hrorm.examples;

import lombok.Data;
import org.hrorm.IndirectKeylessDaoBuilder;
import org.hrorm.query.Field;
import org.hrorm.query.StringField;
import org.hrorm.query.TemporalField;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Keyless {

    //public static final Field<Keyless, String> STRING_FIELD = new Field<>("string")
    public static final StringField<Keyless, String> STRING_FIELD = new StringField<>("string_column", Keyless::getStringColumn, Keyless::setStringColumn);
    public static final Field<Keyless, Long> INTEGER_FIELD = new Field<>("integer_column", Keyless::getIntegerColumn, Keyless::setIntegerColumn);
    public static final Field<Keyless, BigDecimal> DECIMAL_FIELD = new Field<>("decimal_column", Keyless::getDecimalColumn, Keyless::setDecimalColumn);
    public static final Field<Keyless, Boolean> BOOLEAN_FIELD = new Field<>("boolean_column", Keyless::isBooleanColumn, Keyless::setBooleanColumn);
    public static final TemporalField<Keyless, LocalDateTime> TIMESTAMP_FIELD = new TemporalField<>("timestamp_column", Keyless::getTimeStampColumn, Keyless::setTimeStampColumn);

    public static final IndirectKeylessDaoBuilder<Keyless, Keyless> DAO_BUILDER =
            new IndirectKeylessDaoBuilder<>("keyless_table", Keyless::new, x->x)
                    .withStringColumn("string_column", Keyless::getStringColumn, Keyless::setStringColumn)
                    .withIntegerColumn("integer_column", Keyless::getIntegerColumn, Keyless::setIntegerColumn)
                    .withBigDecimalColumn("decimal_column", Keyless::getDecimalColumn, Keyless::setDecimalColumn)
                    .withBooleanColumn("boolean_column", Keyless::isBooleanColumn, Keyless::setBooleanColumn)
                    .withLocalDateTimeColumn("timestamp_column", Keyless::getTimeStampColumn, Keyless::setTimeStampColumn);

    private String stringColumn;
    private long integerColumn;
    private BigDecimal decimalColumn;
    private boolean booleanColumn;
    private LocalDateTime timeStampColumn;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Keyless keyless = (Keyless) o;
        return integerColumn == keyless.integerColumn &&
                booleanColumn == keyless.booleanColumn &&
                Objects.equals(stringColumn, keyless.stringColumn) &&
                Objects.equals(decimalColumn, keyless.decimalColumn) &&
                Objects.equals(timeStampColumn, keyless.timeStampColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringColumn, integerColumn, decimalColumn, booleanColumn, timeStampColumn);
    }
}
