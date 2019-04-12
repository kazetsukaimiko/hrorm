package org.hrorm;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

public class Converters {

    public static Converter<Boolean, String> T_F_BOOLEAN_STRING_CONVERTER = new BooleanStringConverter("T","F");

    public static Converter<Boolean, Long> ONE_ZERO_BOOLEAN_LONG_CONVERTER = new BooleanLongConverter(1, 0);

    public static Converter<Instant, Timestamp> INSTANT_TIMESTAMP_CONVERTER = new InstantTimestampConverter();

    /**
     * This {@link Converter} translates true values to "T" and false
     * values to "F".
     *
     * <p>
     *
     * Most users of hrorm will have no need to directly use this.
     */
    public static class BooleanStringConverter implements Converter<Boolean, String> {

        private final String trueRepresentation;
        private final String falseRepresentation;

        public BooleanStringConverter(String trueRepresentation, String falseRepresentation) {
            this.trueRepresentation = trueRepresentation;
            this.falseRepresentation = falseRepresentation;
        }

        @Override
        public String from(Boolean aBoolean) {
            if ( aBoolean == null ) {
                return null;
            }
            return aBoolean ? trueRepresentation : falseRepresentation;
        }

        @Override
        public Boolean to(String s) {
            if ( s == null ){
                return null;
            }
            if (Objects.equals(s, trueRepresentation)) {
                return Boolean.TRUE;
            } else if (Objects.equals(s, falseRepresentation)) {
                return Boolean.FALSE;
            }
            throw new HrormException("Unsupported string: " + s);
        }
    }

    /**
     * This {@link Converter} translates true values to 1 and false
     * values to 0.
     *
     * <p>
     *
     * Most users of hrorm will have no need to directly use this.
     */
    public static class BooleanLongConverter implements Converter<Boolean, Long> {

        private final long trueRepresentation;
        private final long falseRepresentation;

        public BooleanLongConverter(long trueRepresentation, long falseRepresentation){
            this.trueRepresentation = trueRepresentation;
            this.falseRepresentation = falseRepresentation;
        }

        @Override
        public Long from(Boolean aBoolean) {
            if ( aBoolean == null ) {
                return null;
            }
            return aBoolean ? trueRepresentation : falseRepresentation;
        }

        @Override
        public Boolean to(Long s) {
            if ( s == null ){
                return null;
            }
            if (s == trueRepresentation ){
                return Boolean.TRUE;
            } else if (s == falseRepresentation) {
                return Boolean.FALSE;
            }
            throw new HrormException("Unsupported value: " + s);
        }
    }

    public static class InstantTimestampConverter implements Converter<Instant, Timestamp> {
        @Override
        public Timestamp from(Instant item) {
            if( item == null ){
                return null;
            }
            Timestamp timestamp = Timestamp.from(item);
            return timestamp;
        }

        @Override
        public Instant to(Timestamp timestamp) {
            if ( timestamp == null ) {
                return null;
            }
            Instant instant = timestamp.toInstant();
            return instant;
        }
    }
}
