package org.hrorm;

import org.hrorm.jdbc.interaction.PreparedStatementSetter;
import org.hrorm.jdbc.interaction.ResultSetReader;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GenericColumnWrapper {
    public void test() {


        PreparedStatementSetter<BigDecimal> setter = PreparedStatement::setBigDecimal;

        ResultSetReader<BigDecimal> getter = ResultSet::getBigDecimal;





    }
}
