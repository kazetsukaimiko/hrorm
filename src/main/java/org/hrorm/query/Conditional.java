package org.hrorm.query;

import java.util.List;

public abstract class Conditional<ENTITY> {
    public abstract String sqlFragment();
    public abstract List<Field<ENTITY, ?>> fieldPositions();
}
