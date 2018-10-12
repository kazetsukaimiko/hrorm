package org.hrorm;

import java.sql.Connection;
import java.util.function.Consumer;

public class PopulateResult {
    public static final PopulateResult PrimaryKey = new PopulateResult("Primary Key");
    public static final PopulateResult NoPrimaryKey = new PopulateResult("No Primary Key");
    public static final PopulateResult Ignore = new PopulateResult("Ignore");
    public static final PopulateResult ParentColumn = new PopulateResult("Parent Column");

    private final String code;
    private final Consumer<Connection> connectionUser;

    private PopulateResult(String code){
        this.code = code;
        this.connectionUser = con -> {};
    }

    private PopulateResult(Consumer<Connection> connectionUser){
        this.connectionUser = connectionUser;
        this.code = "Join Column";
    }

    public static PopulateResult fromJoinColumn(Consumer<Connection> connectionUser){
        return new PopulateResult(connectionUser);
    }

    public void populateChildren(Connection connection){
        this.connectionUser.accept(connection);
    }

}
