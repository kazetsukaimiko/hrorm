package org.hrorm;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Some {@link Converter} implementations that hrorm uses internally.
 */
public class Converters {

    /**
     * This <code>Converter</code> translates true values to "T" and false values to "F".
     */
    public static Converter<Boolean, String> T_F_BOOLEAN_STRING_CONVERTER = new BooleanStringConverter("T","F");

    /**
     * This <code>Converter</code> translates true values to 1 and false values to 0.
     */
    public static Converter<Boolean, Long> ONE_ZERO_BOOLEAN_LONG_CONVERTER = new BooleanLongConverter(1, 0);

    public static Converter<Instant, Timestamp> INSTANT_TIMESTAMP_CONVERTER = new InstantTimestampConverter();

    public static class FunctionsConverter<I, T> implements Converter<I, T> {
        private final Function<T, I> toFunction;
        private final Function<I, T> fromFunction;

        FunctionsConverter(Function<T, I> toFunction, Function<I, T> fromFunction) {
            this.toFunction = toFunction;
            this.fromFunction = fromFunction;
        }

        @Override
        public T from(I item) {
            return Optional.ofNullable(item)
                    .map(fromFunction)
                    .orElse(null);
        }

        @Override
        public I to(T value) {
            return Optional.ofNullable(value)
                    .map(toFunction)
                    .orElse(null);
        }
    }

    /**
     * This <code>Converter</code> translates between <code>Boolean</code> values and <code>Long</code> values.
     */
    public static class BooleanConverter<E> extends FunctionsConverter<Boolean, E> {
        public BooleanConverter(E trueRepresentation, E falseRepresentation){
            super(
                    (l) -> Stream.of(Boolean.TRUE, Boolean.FALSE)
                            .filter(b -> Objects.equals(l, b ? trueRepresentation : falseRepresentation))
                            .findFirst()
                            .orElseThrow(() -> new HrormException("Unsupported value: " + l)),
                    (b) -> b ? trueRepresentation : falseRepresentation
            );
        }
    }

    /**
     * This <code>Converter</code> translates between <code>Boolean</code> values and <code>String</code> values.
     */
    public static class BooleanStringConverter extends BooleanConverter<String> {
        public BooleanStringConverter(String trueRepresentation, String falseRepresentation) {
            super(trueRepresentation, falseRepresentation);
        }
    }

    /**
     * This <code>Converter</code> translates between <code>Boolean</code> values and <code>Long</code> values.
     */
    public static class BooleanLongConverter extends BooleanConverter<Long> {
        public BooleanLongConverter(long trueRepresentation, long falseRepresentation){
                super(trueRepresentation, falseRepresentation);
        }
    }



    /**
     * This <code>Converter</code> translates between <code>Instant</code> values and <code>Timestamp</code> values.
     */
    public static class InstantTimestampConverter extends FunctionsConverter<Instant, Timestamp> {
        InstantTimestampConverter() {
            super(Timestamp::toInstant, Timestamp::from);
        }
    }

}
