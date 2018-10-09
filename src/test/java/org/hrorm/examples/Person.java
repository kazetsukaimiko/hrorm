package org.hrorm.examples;

import lombok.Data;
import org.hrorm.Converter;
import org.hrorm.Dao;
import org.hrorm.DaoBuilder;
import org.junit.Assert;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;

@Data
class Person {
    Long id;
    String name;
    long weight;
    BigDecimal height;
    LocalDateTime birthday;
    boolean isHighSchoolGraduate;
    HairColor hairColor;

    private void builderDemo() {
        DaoBuilder<Person> daoBuilder = new DaoBuilder<>("PERSON_TABLE", Person::new);

        daoBuilder.withPrimaryKey("ID","PERSON_SEQUENCE", Person::getId, Person::setId);

        daoBuilder.withStringColumn("NAME", Person::getName, Person::setName);

        daoBuilder.withIntegerColumn("WEIGHT", Person::getWeight, Person::setWeight);

        daoBuilder.withBigDecimalColumn("HEIGHT", Person::getHeight, Person::setHeight);

        daoBuilder.withLocalDateTimeColumn("BIRTHDAY", Person::getBirthday, Person::setBirthday);

        daoBuilder.withBooleanColumn("IS_HIGH_SCHOOL_GRADUATE", Person::isHighSchoolGraduate, Person::setHighSchoolGraduate);

        daoBuilder.withConvertingStringColumn("HAIR_COLOR", Person::getHairColor, Person::setHairColor, new HairColorConverter());
    }

    private void fluidDemo(){
        DaoBuilder<Person> daoBuilder = new DaoBuilder<>("PERSON_TABLE", Person::new)
                .withPrimaryKey("ID","PERSON_SEQUENCE", Person::getId, Person::setId)
                .withStringColumn("NAME", Person::getName, Person::setName)
                .withIntegerColumn("WEIGHT", Person::getWeight, Person::setWeight)
                .withBigDecimalColumn("HEIGHT", Person::getHeight, Person::setHeight)
                .withLocalDateTimeColumn("BIRTHDAY", Person::getBirthday, Person::setBirthday)
                .withBooleanColumn("IS_HIGH_SCHOOL_GRADUATE", Person::isHighSchoolGraduate, Person::setHighSchoolGraduate)
                .withConvertingStringColumn("HAIR_COLOR", Person::getHairColor, Person::setHairColor, new HairColorConverter());

        Connection connection = Mockito.mock(Connection.class);

        Dao<Person> personDao = daoBuilder.buildDao(connection);

        Person person = new Person();
        // set values on the fields but leave the ID null
        long id = personDao.insert(person);
        Assert.assertNotNull(person.getId());
        Assert.assertTrue(id == person.getId());


    }
}

class HairColorConverter implements Converter<HairColor, String> {
    @Override
    public String from(HairColor item) {
        return item.getColorName();
    }

    @Override
    public HairColor to(String s) {
        return HairColor.forColorName(s);
    }
}



