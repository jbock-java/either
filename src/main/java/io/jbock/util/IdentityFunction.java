package io.jbock.util;

public final class IdentityFunction<X, Y> {

    private static final IdentityFunction<?, ?> INSTANCE = new IdentityFunction<>();

    /**
     * This method returns an identity function.
     *
     * @return identity function
     */
    public static <U> IdentityFunction<U, U> identity() {
        @SuppressWarnings("unchecked")
        IdentityFunction<U, U> result = (IdentityFunction<U, U>) INSTANCE;
        return result;
    }

    private IdentityFunction() {
    }
}
