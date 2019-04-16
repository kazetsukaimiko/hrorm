package org.hrorm.examples;

import org.hrorm.HrormException;
import org.hrorm.KeylessDao;
import org.hrorm.KeylessValidator;
import org.hrorm.Operator;
import org.hrorm.Where;
import org.hrorm.database.Helper;
import org.hrorm.database.HelperFactory;
import org.hrorm.examples.keyless.Keyless;
import org.hrorm.util.RandomUtils;
import org.hrorm.util.TestLogConfig;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test operations for lazy behaviors, specifically detached Streams.
 */
public class ReallyLazyTest {

    static { TestLogConfig.load(); }

    private static Helper helper = HelperFactory.forSchema("keyless");

    @BeforeClass
    public static void setUpDb() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        helper.initializeSchema();
    }

    @AfterClass
    public static void cleanUpDb(){
        helper.dropSchema();
    }

    @Test
    public void testStreamLazyEnough() throws SQLException {
        Connection connection = helper.connect();
        KeylessDao<Keyless> dao = Keyless.DAO_BUILDER.buildDao(connection);

        connection.close();

        // This shouldn't create ResultSets or PreparedStatement and thus shouldn't blow up.
        Stream<Keyless> keylessStream = dao.streamAll();
    }

    @Test(expected = HrormException.class)
    public void testLazyResourceInitialization() throws SQLException {
        Connection connection = helper.connect();
        KeylessDao<Keyless> dao = Keyless.DAO_BUILDER.buildDao(connection);

        connection.close();

        Stream<Keyless> keylessStream = dao.streamAll();

        // Attempting to consume is where we should throw, and specifically we should fail
        // trying to create the prepared statement.
        keylessStream.forEach(System.out::println);
    }

}
