package org.hrorm.jdbc.types;

import java.sql.Types;

public class StringType extends ColumnType<String> {
    static StringType CHAR = new StringType(Types.CHAR);
    static StringType NVARCHAR = new StringType(Types.NVARCHAR);
    static StringType LONGNVARCHAR = new StringType(Types.LONGNVARCHAR);
    static StringType LONGVARCHAR = new StringType(Types.LONGVARCHAR);
    static StringType VARCHAR = new StringType(Types.VARCHAR);
    static StringType CLOB = new StringType(Types.CLOB);
    static StringType NCLOB = new StringType(Types.NCLOB);
    static StringType BLOB = new StringType(Types.BLOB);

    protected StringType(int sqlType) {
        super(String.class, sqlType);
    }
}
