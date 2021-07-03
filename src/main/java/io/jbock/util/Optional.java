package io.jbock.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A container object which may or may not contain a non-{@code null} value.
 * This class might be used a drop-in replacement for {@link java.util.Optional},
 * but please note the following differences:
 *
 * <ul>
 *   <li>The {@link #map(Function)} method throws an exception if the mapper
 *       function returns a {@code null} result.
 *   <li>There is no {@code get()} method. Use {@link #orElseThrow()} instead.
 * </ul>
 *
 * <p>In addition to the methods that are basically copied from {@code java.util.Optional},
 * this class contains some methods related to {@link Either},
 * in particular {@link #orElseLeft(Supplier)} and {@link #flatMapLeft(Supplier)}.
 *
 * @param <R> the type of the value
 */
public final class Optional<R> extends AbstractOptional<R> {

    private static final Optional<?> EMPTY = new Optional<>(null);

    private Optional(R value) {
        super(value);
    }

    /**
     * Returns an {@code Optional} containing the given
     * non-{@code null} value.
     *
     * @param value the value, which must be non-{@code null}
     * @param <R> the type of the value
     * @return an {@code Optional} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    public static <R> Optional<R> of(R value) {
        return new Optional<>(Objects.requireNonNull(value));
    }

    /**
     * Returns an empty instance.
     *
     * @param <R> type of the non-existent value
     * @return an empty {@code Optional}
     */
    @SuppressWarnings("unchecked")
    public static <R> Optional<R> empty() {
        return (Optional<R>) EMPTY;
    }

    /**
     * Returns an {@code Optional} containing the given value, if
     * non-{@code null}, otherwise returns an empty {@code Optional}.
     *
     * @param value the possibly-{@code null} value
     * @param <R> the type of the value
     * @return an {@code Optional} with a present value if the specified value
     *         is non-{@code null}, otherwise an empty {@code Optional}
     */
    public static <R> Optional<R> ofNullable(R value) {
        return value == null ? empty() : of(value);
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code Optional} containing the value, otherwise returns an
     * empty {@code Optional}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code Optional} containing the value of this
     *         {@code Optional}, if a value is present and the value matches the
     *         given predicate, otherwise an empty {@code Optional}
     */
    public Optional<R> filter(Predicate<? super R> predicate) {
        if (isEmpty()) {
            return this;
        }
        return predicate.test(orElseThrow()) ? this : empty();
    }

    /**
     * If a value is present, returns an {@code Optional} containing
     * the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code Optional}.
     *
     * <p>If the mapping function returns a {@code null} result,
     * then this method throws a {@code NullPointerException}.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <R2> The type of the value returned from the mapping function
     * @return an {@code Optional} containing the result of applying a mapping
     *         function to the value, if a value is
     *         present, otherwise an empty {@code Optional}
     * @throws NullPointerException if the mapping function returns {@code null}
     */
    public <R2> Optional<R2> map(Function<? super R, ? extends R2> mapper) {
        if (isEmpty()) {
            return empty();
        }
        return of(mapper.apply(orElseThrow()));
    }

    /**
     * If a value is present, returns the result of applying the given
     * mapping function to the value, otherwise returns
     * an empty {@code Optional}.
     *
     * @param <R2> The type of value of the {@code Optional} returned by the
     *             mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying the mapping function to the value, if a value
     *         is present, otherwise an empty {@code Optional}
     */
    public <R2> Optional<R2> flatMap(Function<? super R, Optional<? extends R2>> mapper) {
        if (isEmpty()) {
            return empty();
        }
        return narrow(mapper.apply(orElseThrow()));
    }

    /**
     * If a value is present, returns an {@code Optional} describing the value,
     * otherwise returns an {@code Optional} produced by the supplier.
     *
     * @param supplier the supplier that produces an {@code Optional}
     *        to be returned
     * @return returns an {@code Optional} containing the value of this
     *         {@code Optional}, if a value is present, otherwise an
     *         {@code Optional} produced by the supplier.
     */
    public Optional<R> or(Supplier<? extends Optional<? extends R>> supplier) {
        if (isPresent()) {
            return this;
        }
        return narrow(supplier.get());
    }

    /**
     * If a value is present, returns a Right-{@link Either}
     * containing that value.
     * Otherwise returns a Left-Either containing the value produced
     * by the supplier.
     *
     * @param supplier supplier of a Left value
     * @param <L> the LHS type
     * @return a Right-Either containing the value, if it exists,
     *         or otherwise a Left-Either containing the result of invoking {@code supplier.get()}
     */
    public <L> Either<L, R> orElseLeft(Supplier<? extends L> supplier) {
        return flatMapLeft(() -> Either.left(supplier.get()));
    }

    /**
     * If a value is present, returns a Right-{@link Either} containing
     * that value. Otherwise returns the Either instance produced by
     * the supplier.
     *
     * @param supplier a supplier that yields an Either instance
     * @param <L> the LHS type
     * @return a Right-Either containing the value, if a value is present,
     *         otherwise the result of invoking {@code supplier.get()}
     */
    public <L> Either<L, R> flatMapLeft(
            Supplier<? extends Either<? extends L, ? extends R>> supplier) {
        if (isPresent()) {
            return Either.right(orElseThrow());
        }
        return Either.narrow(supplier.get());
    }

    /**
     * Returns a string representation of this {@code Optional}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return isPresent()
                ? String.format("Optional[%s]", orElseThrow())
                : "Optional.empty";
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Optional}.
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object
     *         otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Optional)) {
            return false;
        }

        Optional<?> other = (Optional<?>) obj;
        return isEqual(other);
    }

    /**
     * Internal helper method that narrows the type of {@code Optional<? extends T>} to
     * {@code Optional<T>}.
     *
     * @param optional an optional
     * @param <T> the type of the value
     * @return an equivalent {@code Optional} instance
     */
    @SuppressWarnings("unchecked")
    private static <T> Optional<T> narrow(Optional<? extends T> optional) {
        // The cast is just an optimization of the following:
        // return optional.map(Optional::<T>of).orElse(Optional.empty())
        return (Optional<T>) optional;
    }
}
