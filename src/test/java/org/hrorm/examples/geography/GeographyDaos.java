package org.hrorm.examples.geography;

import org.hrorm.DaoBuilder;

public class GeographyDaos {

    public static DaoBuilder<State> StateDaoBuilder = new DaoBuilder<>("STATE", State::new)
            .withPrimaryKey("ID", "STATE_SEQUENCE", State::getId, State::setId)
            .withStringColumn("NAME", State::getName, State::setName);


    public static DaoBuilder<City> CityDaoBuilder = new DaoBuilder<>("CITY", City::new)
            .withPrimaryKey("ID", "CITY_SEQUENCE", City::getId, City::setId)
            .withStringColumn("NAME", City::getName, City::setName)
            .withJoinColumn("STATE_ID", City::getState, City::setState, StateDaoBuilder);
}
