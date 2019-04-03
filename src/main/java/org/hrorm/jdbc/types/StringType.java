package org.hrorm.jdbc.types;

import java.sql.Types;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringType extends ColumnType<String> {
    public static StringType CHAR(String sqlTypeName) { return new StringType(Types.CHAR, sqlTypeName); }
    public static StringType NVARCHAR(String sqlTypeName) { return new StringType(Types.NVARCHAR, sqlTypeName); }
    public static StringType LONGNVARCHAR(String sqlTypeName) { return new StringType(Types.LONGNVARCHAR, sqlTypeName); }
    public static StringType LONGVARCHAR(String sqlTypeName) { return new StringType(Types.LONGVARCHAR, sqlTypeName); }

    public static StringType VARCHAR(String sqlTypeName) { return new StringType(Types.VARCHAR, sqlTypeName); }
    public static StringType CLOB(String sqlTypeName) { return new StringType(Types.CLOB, sqlTypeName); }
    public static StringType NCLOB(String sqlTypeName) { return new StringType(Types.NCLOB, sqlTypeName); }
    public static StringType BLOB(String sqlTypeName) { return new StringType(Types.BLOB, sqlTypeName); }

    private StringType(int sqlType, String sqlTypeName) {
        super(String.class, sqlType, sqlTypeName);
    }

    @Override
    public Set<Integer> supportedTypes() {
        return ColumnTypes.StringTypes;
    }
}
