package org.hrorm;

public interface UncheckedCloseable extends Runnable, AutoCloseable {
    default void run() {
        try { close(); } catch (Exception ex) { throw new RuntimeException(ex); }
    }
    static UncheckedCloseable wrap(AutoCloseable autoCloseable) {
        return autoCloseable::close;
    }
    default UncheckedCloseable nest(AutoCloseable autoCloseable) {
        return () -> { try(UncheckedCloseable uncheckedCloseable=this) { uncheckedCloseable.close();}};
    }
}
