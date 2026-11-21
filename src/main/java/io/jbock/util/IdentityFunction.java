package io.jbock.util;

public final class IdentityFunction<X, Y> {

    /**
     * This method returns {@code null}.
     *
     * @return null
     */
    public static <U> IdentityFunction<U, U> identity() {
        return null;
    }

    // no instances
    private IdentityFunction() {
    }
}
