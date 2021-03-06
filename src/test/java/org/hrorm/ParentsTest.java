package org.hrorm;

import org.hrorm.database.Helper;
import org.hrorm.database.HelperFactory;
import org.hrorm.examples.parentage.Child;
import org.hrorm.examples.EnumeratedColor;
import org.hrorm.examples.parentage.Grandchild;
import org.hrorm.examples.parentage.Parent;
import org.hrorm.examples.parentage.ParentChildBuilders;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParentsTest {

    private static Helper helper = HelperFactory.forSchema("parents");

    @BeforeClass
    public static void setUpDb(){
        helper.initializeSchema();
    }

    @AfterClass
    public static void cleanUpDb(){
        helper.dropSchema();
    }

    @Test
    public void testSavePropagatesToChildren() throws SQLException {
        Child child = new Child();
        child.setNumber(123L);

        Parent parent = new Parent();
        parent.setName("save propagation test");
        parent.setChildList(Arrays.asList(child));

        Connection connection = helper.connect();

        Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

        parentDao.insert(parent);

        long parentId = parent.getId();

        Assert.assertTrue(parentId > 0);

        Parent readItem = parentDao.select(parentId);

        Assert.assertNotNull(readItem);
        Assert.assertEquals("save propagation test", readItem.getName());
        Assert.assertEquals(1, readItem.getChildList().size());
        Assert.assertEquals(123L, (long) readItem.getChildList().get(0).getNumber());
        Assert.assertTrue(readItem.getChildList().get(0).getId() > 1);

        connection.commit();
        connection.close();
    }

    @Test
    public void testDeletesHappenOnUpdate() throws SQLException {
        Connection connection = helper.connect();
        Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

        Child child = new Child();
        child.setNumber(123L);

        Parent parent = new Parent();
        parent.setName("propagated deletes test");
        parent.setChildList(Arrays.asList(child));

        long id = parentDao.insert(parent);

        Parent readParent = parentDao.select(id);

        Assert.assertEquals(1, readParent.getChildList().size());

        readParent.setChildList(Collections.emptyList());

        parentDao.update(readParent);

        Parent readAfterUpdate = parentDao.select(id);

        Assert.assertEquals(0, readAfterUpdate.getChildList().size());

        connection.commit();
        connection.close();
    }

    @Test
    public void testUpdatesPropagate() throws SQLException {
        Connection connection = helper.connect();
        Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

        Child child = new Child();
        child.setNumber(123L);

        Parent parent = new Parent();
        parent.setName("propagated updates test");
        parent.setChildList(Arrays.asList(child));

        long id = parentDao.insert(parent);

        Parent readParent = parentDao.select(id);

        Assert.assertEquals(1, readParent.getChildList().size());
        Assert.assertEquals(123L, (long) readParent.getChildList().get(0).getNumber());

        readParent.getChildList().get(0).setNumber(55L);

        parentDao.update(readParent);

        Parent readAfterUpdate = parentDao.select(id);

        Assert.assertEquals(55L, (long) readAfterUpdate.getChildList().get(0).getNumber());

        connection.commit();
        connection.close();
    }

    @Test
    public void testSavePropagatesToGrandChildren() throws SQLException {
        Grandchild grandchild = new Grandchild();
        grandchild.setColor(EnumeratedColor.Green);

        Child child = new Child();
        child.setNumber(123L);
        child.setGrandchildList(Arrays.asList(grandchild));

        Parent parent = new Parent();
        parent.setName("save multigeneration test");
        parent.setChildList(Arrays.asList(child));

        Connection connection = helper.connect();

        Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

        parentDao.insert(parent);

        long parentId = parent.getId();

        Parent readItem = parentDao.select(parentId);

        Assert.assertEquals(1, readItem.getChildList().get(0).getGrandchildList().size());
        Assert.assertEquals(EnumeratedColor.Green,  readItem.getChildList().get(0).getGrandchildList().get(0).getColor());
        Assert.assertTrue(readItem.getChildList().get(0).getGrandchildList().get(0).getId() > 1);

        connection.commit();
        connection.close();
    }

    @Test
    public void testUpdatesPropagatesToGrandChildren() throws SQLException {
        long parentId;
        {
            Grandchild grandchild = new Grandchild();
            grandchild.setColor(EnumeratedColor.Green);

            Child child = new Child();
            child.setNumber(123L);
            child.setGrandchildList(Arrays.asList(grandchild));

            Parent parent = new Parent();
            parent.setName("update multigeneration test");
            parent.setChildList(Arrays.asList(child));

            Connection connection = helper.connect();
            Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

            parentId = parentDao.insert(parent);

            connection.commit();
            connection.close();
        }
        {
            Connection connection = helper.connect();
            Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
            Parent readItem = parentDao.select(parentId);

            Assert.assertEquals(1, readItem.getChildList().get(0).getGrandchildList().size());
            Assert.assertEquals(EnumeratedColor.Green, readItem.getChildList().get(0).getGrandchildList().get(0).getColor());

            readItem.getChildList().get(0).getGrandchildList().get(0).setColor(EnumeratedColor.Blue);

            parentDao.update(readItem);

            connection.commit();
            connection.close();
        }
        {
            Connection connection = helper.connect();
            Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
            Parent secondReadItem = parentDao.select(parentId);

            Assert.assertNotNull(secondReadItem);
            Assert.assertEquals(1, secondReadItem.getChildList().size());

            Assert.assertEquals(1, secondReadItem.getChildList().get(0).getGrandchildList().size());
            Assert.assertEquals(EnumeratedColor.Blue, secondReadItem.getChildList().get(0).getGrandchildList().get(0).getColor());

            connection.close();
        }

    }

    @Test
    public void deletionOfChildrenDoesNotOrphanGrandchildRecords() throws SQLException {

        Grandchild grandchild = new Grandchild();
        grandchild.setColor(EnumeratedColor.Green);

        Child child = new Child();
        child.setNumber(123L);
        child.setGrandchildList(Arrays.asList(grandchild));

        Parent parent = new Parent();
        parent.setName("delete orphan grandchildren test");
        parent.setChildList(Arrays.asList(child));

        Connection connection = helper.connect();

        Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

        parentDao.insert(parent);

        long parentId = parent.getId();

        Parent readItem = parentDao.select(parentId);

        Assert.assertEquals(1, readItem.getChildList().get(0).getGrandchildList().size());
        Assert.assertEquals(EnumeratedColor.Green,  readItem.getChildList().get(0).getGrandchildList().get(0).getColor());

        Dao<Grandchild> grandchildDao = ParentChildBuilders.GrandchildDaoBuilder.buildDao(connection);
        List<Grandchild> allGrandchildren = grandchildDao.selectAll();

        Assert.assertEquals(1, allGrandchildren.size());

        readItem.setChildList(Collections.emptyList());

        parentDao.update(readItem);

        Parent secondReadItem = parentDao.select(parentId);

        Assert.assertEquals(0, secondReadItem.getChildList().size());

        allGrandchildren = grandchildDao.selectAll();
        Assert.assertEquals(0, allGrandchildren.size());

        connection.commit();
        connection.close();
    }

    @Test
    public void testInsertMultipleChildren() throws SQLException {

        for( int idx = 0 ; idx< 10 ; idx++ ) {
            long parentId;
            {
                Child childA = new Child();
                childA.setNumber(23L +idx);
                Child childB = new Child();
                childB.setNumber(46L +idx);
                Child childC = new Child();
                childC.setNumber(72L +idx);

                Parent parent = new Parent();
                parent.setName("Multi Child Parent");
                parent.setChildList(Arrays.asList(childA, childB, childC));

                Connection connection = helper.connect();
                Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
                parentDao.insert(parent);
                parentId = parent.getId();

                connection.commit();
                connection.close();
            }
            {
                Connection connection = helper.connect();
                Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

                Parent readItem = parentDao.select(parentId);

                Assert.assertEquals(3, readItem.getChildList().size());

                List<Long> numbers = readItem.getChildList().stream()
                        .map(c -> c.getNumber()).collect(Collectors.toList());

                Assert.assertTrue(numbers.contains(23L +idx));
                Assert.assertTrue(numbers.contains(46L +idx));
                Assert.assertTrue(numbers.contains(72L +idx));

                connection.close();
            }
        }
    }

    @Test
    public void testUpdateMultipleChildren() throws SQLException {

        {
            for (int idx = 0 ; idx< 10; idx++){
                Parent parent = new Parent();
                parent.setName("Multi Child Parent " + idx);

                Connection connection = helper.connect();
                Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
                parentDao.insert(parent);

                connection.commit();
                connection.close();
            }
        }

        for (int idx = 0 ; idx < 5 ; idx++ ) {
            long parentId;
            {
                Child childA = new Child();
                childA.setNumber(23L + idx);
                Child childB = new Child();
                childB.setNumber(46L + idx);
                Child childC = new Child();
                childC.setNumber(72L + idx);

                Parent parent = new Parent();
                parent.setName("Multi Child Parent");
                parent.setChildList(Arrays.asList(childA, childB, childC));

                Connection connection = helper.connect();
                Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
                parentDao.insert(parent);
                parentId = parent.getId();

                connection.commit();
                connection.close();
            }

            {

                Connection connection = helper.connect();
                Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

                Parent readItem = parentDao.select(parentId);

                Assert.assertEquals(3, readItem.getChildList().size());

                Child child46 = readItem.getChildByNumber(46L + idx);
                child46.setNumber(146L);

                Child child23 = readItem.getChildByNumber(23L + idx);
                readItem.getChildList().remove(child23);

                Child child98 = new Child();
                child98.setNumber(98L + idx);

                Child child54 = new Child();
                child54.setNumber(54L + idx);

                readItem.getChildList().add(child54);
                readItem.getChildList().add(child98);

                parentDao.update(readItem);

                connection.commit();
                connection.close();
            }

            {
                Connection connection = helper.connect();
                Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);

                Parent readItem = parentDao.select(parentId);

                Assert.assertEquals(4, readItem.getChildList().size());
                List<Long> numbers = readItem.getChildList().stream()
                        .map(c -> c.getNumber()).collect(Collectors.toList());

                Assert.assertTrue(numbers.contains(146L));
                Assert.assertTrue(numbers.contains(72L + idx));
                Assert.assertTrue(numbers.contains(54L + idx));
                Assert.assertTrue(numbers.contains(98L + idx));

                connection.close();
            }

        }
    }

    @Test
    public void testDaoValidation() throws SQLException {
        Connection connection = helper.connect();
        Validator.validate(connection, ParentChildBuilders.ParentDaoBuilder);
        Validator.validate(connection, ParentChildBuilders.ChildDaoBuilder);
        Validator.validate(connection, ParentChildBuilders.GrandchildDaoBuilder);
        connection.close();
    }

    @Test
    public void testCanInsertSomethingWithParentDirectly() throws SQLException {
        Parent parent;
        {
            parent = new Parent();
            parent.setName("DirectlySaveChildTest");

            Connection connection = helper.connect();
            Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
            parentDao.insert(parent);

            connection.commit();
            connection.close();

        }
        Assert.assertNotNull(parent.getId());
        long childId;
        {
            Child child = new Child();
            child.setParent(parent);
            child.setNumber(123L);

            Grandchild g1 = new Grandchild();
            g1.setColor(EnumeratedColor.Blue);

            Grandchild g2 = new Grandchild();
            g2.setColor(EnumeratedColor.Red);

            child.setGrandchildList(Arrays.asList(g1, g2));

            Connection connection = helper.connect();

            Dao<Child> childDao = ParentChildBuilders.ChildDaoBuilder.buildDao(connection);

            childId = childDao.insert(child);

            connection.commit();
            connection.close();
        }
        {
            Connection connection = helper.connect();

            Dao<Child> childDao = ParentChildBuilders.ChildDaoBuilder.buildDao(connection);
            Child child = childDao.select(childId);
            Assert.assertEquals(2, child.getGrandchildList().size());
            Assert.assertEquals(123L, (long) child.getNumber());
            Assert.assertNull(child.getParent());
            connection.commit();
            connection.close();
        }

    }


    @Test
    public void testCanUpdateSomethingWithParentDirectly() throws SQLException {
        long childId;
        Parent parent;
        {
            Grandchild grandchild = new Grandchild();
            grandchild.setColor(EnumeratedColor.Blue);

            Child child = new Child();
            child.setNumber(4321L);
            child.setGrandchildList(Collections.singletonList(grandchild));

            parent = new Parent();
            parent.setName("Directly Update Child Test");
            parent.setChildList(Collections.singletonList(child));

            Connection connection = helper.connect();
            Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
            parentDao.insert(parent);

            childId = child.getId();

            connection.commit();
            connection.close();
        }
        {
            Connection connection = helper.connect();
            Dao<Child> childDao = ParentChildBuilders.ChildDaoBuilder.buildDao(connection);
            Child child = childDao.select(childId);

            Assert.assertNull(child.getParent());

            child.setNumber(45325L);
            child.setParent(parent);

            childDao.update(child);

            connection.commit();
            connection.close();
        }
        {
            Connection connection = helper.connect();
            Dao<Child> childDao = ParentChildBuilders.ChildDaoBuilder.buildDao(connection);
            Child child = childDao.select(childId);
            Assert.assertEquals(45325L, (long) child.getNumber());

            connection.close();
        }

    }

    @Test
    public void parentIsSetOnChildOnLoad() throws SQLException {

        long parentId;
        {
            Parent parent = new Parent();
            parent.setName("ParentIsSetOnChildOnLoadTest");

            Child child = new Child();
            child.setNumber(34L);

            parent.setChildList(Collections.singletonList(child));

            Connection connection = helper.connect();
            Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
            parentId = parentDao.insert(parent);

            connection.commit();
            connection.close();
        }
        {
            Connection connection = helper.connect();
            Dao<Parent> parentDao = ParentChildBuilders.ParentDaoBuilder.buildDao(connection);
            Parent parent = parentDao.select(parentId);

            Child child = parent.getChildByNumber(34L);

            Assert.assertNotNull(child);
            Assert.assertEquals(parent, child.getParent());

            connection.close();
        }
    }
}
