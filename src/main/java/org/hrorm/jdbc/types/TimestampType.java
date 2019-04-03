package org.hrorm.jdbc.types;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Set;

public class TimestampType extends ColumnType<Timestamp> {
    public static TimestampType TIMESTAMP(String sqlTypeName) { return new TimestampType(Types.TIMESTAMP, sqlTypeName); }

    private TimestampType(int sqlType, String sqlTypeName) {
        super(Timestamp.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return Collections.singleton(Types.TIMESTAMP);
    }
}
