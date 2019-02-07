package org.hrorm.query;

import org.hrorm.KeylessDao;

public class KeylessQueryBuilder<ENTITY> {
    private final KeylessDao<ENTITY> dao;

    public KeylessQueryBuilder(KeylessDao<ENTITY> dao) {
        this.dao = dao;
    }
}
