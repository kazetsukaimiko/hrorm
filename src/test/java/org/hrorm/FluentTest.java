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
        // The Keyless.class Generic on Compound is populated down to Field-
        // This allows us to enforce field() to limit to methods on the Keyless class, specifically
        // ones that produce the type of value we are looking for in equalTo/lessThan/greaterThan.
        Compound<Keyless> keylessCompund = new Compound<>(Keyless.class)
                .field("string_column", Keyless::getStringColumn)
                .equalTo("value")
                .and() // Can be omitted if you're in a string of ands
                .field("integer_column", Keyless::getIntegerColumn)
                .equalTo(0L)
                .or()
                    .field("timeStampColumn", Keyless::getTimeStampColumn)
                    .lessThan(LocalDateTime.now().minus(Duration.of(4, ChronoUnit.DAYS)));


        // Thinking:
        // Dao<Keyless> keylessDao = //...
        /*
        List<String>keylessDao.selectComplex()
                .field("string_column", Keyless::getStringColumn)
                .equalTo("value")
                .and() // Can be omitted if you're in a string of ands
                .field("integer_column", Keyless::getIntegerColumn)
                .equalTo(0L)
                .execute()
                */

    }
}
