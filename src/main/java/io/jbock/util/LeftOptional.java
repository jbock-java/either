package io.jbock.util;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container object which may or may not contain a non-{@code null} value.
 * This class is a decorator for an instance of {@link java.util.Optional}.
 *
 * <p>In addition to methods from {@code java.util.Optional},
 * this class contains two additional convenience methods:
 * {@link #orElseRight(Supplier)} and {@link #flatMapRight(Supplier)}.
 *
 * @param <T> the type of value
 */
public final class LeftOptional<T> {

    private static final LeftOptional<?> EMPTY = new LeftOptional<>(Optional.empty());

    private final Optional<T> value;

    private LeftOptional(Optional<T> value) {
        this.value = value;
    }

    /**
     * Returns an empty {@code LeftOptional} instance.  No value is present for this
     * {@code LeftOptional}.
     *
     * @apiNote
     * Though it may be tempting to do so, avoid testing if an object is empty
     * by comparing with {@code ==} or {@code !=} against instances returned by
     * {@code Optional.empty()}.  There is no guarantee that it is a singleton.
     * Instead, use {@link #isEmpty()} or {@link #isPresent()}.
     *
     * @param <T> The type of the non-existent value
     * @return an empty {@code LeftOptional}
     */
    public static <T> LeftOptional<T> empty() {
        @SuppressWarnings("unchecked")
        LeftOptional<T> t = (LeftOptional<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the described value.
     *
     * @param value the value to describe; it's the caller's responsibility to
     *        ensure the value is non-{@code null} unless creating the singleton
     *        instance returned by {@code empty()}.
     */
    private LeftOptional(T value) {
        this.value = Optional.of(value);
    }

    /**
     * Returns a {@code LeftOptional} describing the given non-{@code null}
     * value.
     *
     * @param value the value to describe, which must be non-{@code null}
     * @param <T> the type of the value
     * @return a {@code LeftOptional} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    public static <T> LeftOptional<T> of(T value) {
        return new LeftOptional<>(Optional.of(value));
    }

    /**
     * Returns a {@code LeftOptional} describing the given value, if
     * non-{@code null}, otherwise returns an empty {@code LeftOptional}.
     *
     * @param value the possibly-{@code null} value to describe
     * @param <T> the type of the value
     * @return a {@code LeftOptional} with a present value if the specified value
     *         is non-{@code null}, otherwise an empty {@code LeftOptional}
     */
    @SuppressWarnings("unchecked")
    public static <T> LeftOptional<T> ofNullable(T value) {
        return value == null ? (LeftOptional<T>) EMPTY
                : new LeftOptional<>(value);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @apiNote
     * The preferred alternative to this method is {@link #orElseThrow()}.
     *
     * @return the non-{@code null} value described by this {@code LeftOptional}
     * @throws NoSuchElementException if no value is present
     */
    public T get() {
        return value.get();
    }

    /**
     * If a value is present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value.isPresent();
    }

    /**
     * If a value is  not present, returns {@code true}, otherwise
     * {@code false}.
     *
     * @return  {@code true} if a value is not present, otherwise {@code false}
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     * @throws NullPointerException if value is present and the given action is
     *         {@code null}
     */
    public void ifPresent(Consumer<? super T> action) {
        value.ifPresent(action);
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     *        present
     * @throws NullPointerException if a value is present and the given action
     *         is {@code null}, or no value is present and the given empty-based
     *         action is {@code null}.
     */
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        value.ifPresentOrElse(action, emptyAction);
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns a {@code LeftOptional} describing the value, otherwise returns an
     * empty {@code LeftOptional}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return a {@code LeftOptional} describing the value of this
     *         {@code LeftOptional}, if a value is present and the value matches the
     *         given predicate, otherwise an empty {@code LeftOptional}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public LeftOptional<T> filter(Predicate<? super T> predicate) {
        return value.filter(predicate).map(LeftOptional::of).orElse(empty());
    }

    /**
     * If a value is present, returns a {@code LeftOptional} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code LeftOptional}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code LeftOptional}.
     *
     * @apiNote
     * This method supports post-processing on {@code LeftOptional} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code Optional<Path>}:
     *
     * <pre>{@code
     *     Optional<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code Optional<URI>}, and then
     * {@code map} returns an {@code Optional<Path>} for the desired
     * URI if one exists.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the value returned from the mapping function
     * @return a {@code LeftOptional} describing the result of applying a mapping
     *         function to the value of this {@code LeftOptional}, if a value is
     *         present, otherwise an empty {@code LeftOptional}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> LeftOptional<U> map(Function<? super T, ? extends U> mapper) {
        return value.<U>map(mapper).map(LeftOptional::of).orElse(empty());
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code LeftOptional}-bearing mapping function to the value, otherwise returns
     * an empty {@code LeftOptional}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already a {@code LeftOptional}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code LeftOptional}.
     *
     * @param <U> The type of value of the {@code LeftOptional} returned by the
     *            mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying a {@code LeftOptional}-bearing mapping
     *         function to the value of this {@code LeftOptional}, if a value is
     *         present, otherwise an empty {@code LeftOptional}
     * @throws NullPointerException if the mapping function is {@code null} or
     *         returns a {@code null} result
     */
    public <U> LeftOptional<U> flatMap(Function<? super T, ? extends LeftOptional<? extends U>> mapper) {
        return value.flatMap(t -> {
            Optional<? extends U> result = mapper.apply(t).map(Optional::of).orElse(Optional.empty());
            @SuppressWarnings("unchecked")
            Optional<U> narrowed = (Optional<U>) result;
            return narrowed;
        }).map(LeftOptional::of).orElse(empty());
    }

    /**
     * If a value is present, returns a {@code LeftOptional} describing the value,
     * otherwise returns a {@code LeftOptional} produced by the supplying function.
     *
     * @param supplier the supplying function that produces a {@code LeftOptional}
     *        to be returned
     * @return returns a {@code LeftOptional} describing the value of this
     *         {@code LeftOptional}, if a value is present, otherwise an
     *         {@code LeftOptional} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     */
    public LeftOptional<T> or(Supplier<? extends LeftOptional<? extends T>> supplier) {
        return value.or(() -> {
            Optional<? extends T> result = supplier.get().map(Optional::of).orElse(Optional.empty());
            @SuppressWarnings("unchecked")
            Optional<T> narrowed = (Optional<T>) result;
            return narrowed;
        }).map(LeftOptional::of).orElse(empty());
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote
     * This method can be used to transform a {@code Stream} of optional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<Optional<T>> os = ..
     *     Stream<T> s = os.flatMap(Optional::stream)
     * }</pre>
     *
     * @return the optional value as a {@code Stream}
     */
    public Stream<T> stream() {
        return value.stream();
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * {@code other}.
     *
     * @param other the value to be returned, if no value is present.
     *        May be {@code null}.
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value.orElse(other);
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     *         supplying function
     * @throws NullPointerException if no value is present and the supplying
     *         function is {@code null}
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return value.orElseGet(supplier);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code LeftOptional}
     * @throws NoSuchElementException if no value is present
     */
    public T orElseThrow() {
        return value.orElseThrow();
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @apiNote
     * A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *        exception to be thrown
     * @return the value, if present
     * @throws X if no value is present
     * @throws NullPointerException if no value is present and the exception
     *          supplying function is {@code null}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return value.orElseThrow(exceptionSupplier);
    }

    /**
     * If a value is present, returns a Left-{@link Either}
     * containing that value.
     * Otherwise returns a Right-Either containing the value produced
     * by the supplier.
     *
     * @param supplier supplier of a Right value
     * @param <R> the RHS type
     * @return if the value is present, a Left containing that value,
     *         otherwise a Right containing the result of invoking {@code supplier.get()}
     */
    public <R> Either<T, R> orElseRight(Supplier<? extends R> supplier) {
        return value.<Either<T, R>>map(Either::left)
                .orElseGet(() -> Either.right(supplier.get()));
    }

    /**
     * If a value is present, return a Left-{@link Either} containing
     * that value. Otherwise returns the Either instance produced by
     * the supplier.
     *
     * @param supplier a supplier that yields an Either instance
     * @param <R> the RHS type
     * @return a Left-Either containing the value, or if no value is present,
     *         the result of invoking {@code supplier.get()}
     */
    public <R> Either<T, R> flatMapRight(
            Supplier<? extends Either<? extends T, ? extends R>> supplier) {
        return value.<Either<T, R>>map(Either::left).orElseGet(() -> {
            Either<? extends T, ? extends R> result = supplier.get();
            return Either.narrow(result);
        });
    }

    /**
     * Indicates whether some other object is "equal to" this {@code LeftOptional}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also a {@code LeftOptional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
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

        if (!(obj instanceof LeftOptional)) {
            return false;
        }

        LeftOptional<?> other = (LeftOptional<?>) obj;
        return value.equals(other.value);
    }

    /**
     * Returns the hash code of the value, if present, otherwise {@code 0}
     * (zero) if no value is present.
     *
     * @return hash code value of the present value or {@code 0} if no value is
     *         present
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Returns a non-empty string representation of this {@code LeftOptional}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @implSpec
     * If a value is present the result must include its string representation
     * in the result.  Empty and present {@code LeftOptional}s must be unambiguously
     * differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return value.map(v -> String.format("LeftOptional[%s]", v))
                .orElse("LeftOptional.empty");
    }
}
