package org.hrorm;

import org.hrorm.examples.Keyless;
import org.hrorm.query.Compound;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FluentTest {

    @Test
    public void fluentExample() {
/*
        private String stringColumn;
        private long integerColumn;
        private BigDecimal decimalColumn;
        private boolean booleanColumn;
        private LocalDateTime timeStampColumn;
        */

        Compound<Keyless> keylessCompund = new Compound<>(Keyless.class)
                .field("string_column", Keyless::getStringColumn)
                .equalTo("value")
                .and() // Can be omitted if you're in a string of ands
                .field("integer_column", Keyless::getIntegerColumn)
                .equalTo(0L)
                .field("timeStampColumn", Keyless::getTimeStampColumn)
                .lessThan(LocalDateTime.now().minus(Duration.of(4, ChronoUnit.DAYS)));
    }
}
