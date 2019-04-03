package org.hrorm.jdbc.types;

import java.sql.Date;
import java.sql.Types;
import java.util.Collections;
import java.util.Set;

public class DateType extends ColumnType<Date> {
    public static DateType DATE(String sqlTypeName) {
        return new DateType(Types.DATE, sqlTypeName);
    }

    protected DateType(int sqlType, String sqlTypeName) {
        super(Date.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return Collections.singleton(Types.DATE);
    }
}
