package org.hrorm.jdbc.types;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Set;

public class TimestampType extends ColumnType<Timestamp> {
    public static final TimestampType TIMESTAMP = new TimestampType(Types.TIMESTAMP);

    protected TimestampType(int sqlType) {
        this(sqlType, null);    }

    protected TimestampType(int sqlType, String sqlTypeName) {
        super(Timestamp.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return Collections.singleton(Types.TIMESTAMP);
    }
}
