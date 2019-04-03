package org.hrorm.jdbc.types;

import java.sql.Time;
import java.sql.Types;

public class TimeType extends ColumnType<Time> {
    public static final TimeType TIME = new TimeType(Types.TIME);

    protected TimeType(int sqlType) {
        this(sqlType, null);    }

    protected TimeType(int sqlType, String sqlTypeName) {
        super(Time.class, sqlType, sqlTypeName);
    }
}
