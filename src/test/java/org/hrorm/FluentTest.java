package org.hrorm;

import org.hrorm.examples.Keyless;
import org.hrorm.h2.H2Helper;
import org.hrorm.util.RandomUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FluentTest {

    private static H2Helper helper = new H2Helper("keyless");

    // Make between 500-2000 random Keyless.
    // May seem high, but random sample behavior is better tested with a good number of entities.
    private static final List<Keyless> fakeEntities = RandomUtils.randomNumberOf(500, 2000, KeylessTest::randomKeyless);

    @BeforeClass
    public static void setUpDb(){
        helper.initializeSchema();

        // Insertion Phase
        Connection connection = helper.connect();
        KeylessDao<Keyless> dao = Keyless.DAO_BUILDER.buildDao(connection);
        fakeEntities.forEach(dao::insert);
    }

    @AfterClass
    public static void cleanUpDb(){
        helper.dropSchema();
    }

    @Test
    public void fluentExample() {
        Connection connection = helper.connect();

        LocalDateTime threeDaysAgo = LocalDateTime.now().minus(Duration.of(3, ChronoUnit.DAYS));

        KeylessDao<Keyless> dao = Keyless.DAO_BUILDER.buildDao(connection);
        List<Keyless> keyless = dao.select(
                Keyless.STRING_FIELD.equalTo("name")
                    .and(Keyless.BOOLEAN_FIELD.equalTo(false))
                .or(
                        Keyless.TIMESTAMP_FIELD.isAfter(threeDaysAgo)
                )
        );


    }
}
