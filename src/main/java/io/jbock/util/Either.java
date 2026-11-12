package io.jbock.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class serves as a container for a value of unknown type.
 *
 * <p>An Either could be a &quot;Left&quot; or a &quot;Right&quot;,
 * but it can not be &quot;neither&quot; or &quot;both&quot;.
 *
 * <p>An Either always contains exactly one value, of unknown type,
 * and we will refer to it as <em>the</em> value.
 *
 * <p>An Either is often used to express success or failure.
 * If used in this way, by convention,
 * a Right would contain the result of a successful operation,
 * and a Left would contain an object that represents failure.
 *
 * @param <L> the type of the value, if this is a Right
 * @param <R> the type of the value, if this is a Left
 */
public sealed interface Either<L, R> permits Left, Right {

    /**
     * This method returns {@code null}.
     *
     * @return null
     */
    static <U> Function<U, U> identity() {
        return null;
    }

    /**
     * This is used to convert from (bounded) wildcard Right-type
     * to normal (non-wildcard) types, and vice versa.
     *
     * <p>Mapping with {@link Function#identity()} has the same effect,
     * but this avoids creating a new Right-instance.
     *
     * <p>Note: The runtime value of the argument is ignored.
     *
     * <p>Note: The second type argument of the argument function must be
     * equivalent to {@code R}.
     *
     * @param identity a function with return type {@code R}
     * @return the receiver
     * @param <T> new Right-Type; this should be equivalent to {@code R}.
     */
    <T> Either<L, T> mapIdentity(Function<R, ? extends T> identity);

    /**
     * Returns a Left containing the given value.
     *
     * @param value the value
     * @param <L> the type of the value
     * @param <R> any arbitrary type
     * @return a Left containing the value
     */
    static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    /**
     * Returns a Right containing the given value.
     *
     * @param value the value
     * @param <L> any arbitrary type
     * @param <R> the type of the value
     * @return a Right containing the value
     */
    static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    /**
     * If this is a Right, returns a Right containing the result of applying
     * the mapper function to the value.
     *
     * <p>If this is a Left, returns the receiver.
     *
     * @param mapper the function to apply to the value, if this is a Right
     * @param <R2> type of the new value
     * @return either the receiver, or a Right containing
     *         the result of applying {@code mapper} to the value
     */
    <R2> Either<L, R2> map(
            Function<? super R, ? extends R2> mapper);

    /**
     * If this is a Left, returns the receiver.
     *
     * <p>If this is a Right and the predicate function returns an empty result,
     * returns the receiver.
     *
     * <p>If this is a Right and the predicate function returns a non-empty result,
     * returns a Left containing the result of the predicate function.
     *
     * @param filter a function that acts as a filter
     * @return filter result
     */
    Either<L, R> filter(
            Function<? super R, Optional<? extends L>> filter);

    /**
     * <p>If this is a Right, return the result of applying the mapper
     * to the value.
     *
     * <p>If this is a Left, returns the receiver.
     *
     * @param mapper a function that acts as a combination of mapper and filter
     * @return mapper result
     */
    <R2> Either<L, R2> filterMap(
            Function<? super R, ? extends Either<L, ? extends R2>> mapper);

    /**
     * If this is a Left, returns a Left containing the result of applying the mapper function to the value.
     *
     * <p>If this is a Right, returns the receiver.
     *
     * @param mapper the function to apply to the value
     * @param <L2> type of the new value
     * @return either the receiver, or a Left containing
     *         the result of applying {@code mapper} to the value
     */
    <L2> Either<L2, R> mapLeft(
            Function<? super L, ? extends L2> mapper);

    /**
     * If this is a Left, returns the receiver.
     *
     * <p>If this is a Right, an exception is thrown.
     *
     * @return if this is a Left, the same object unchanged
     * @throws UnsupportedOperationException if this is a Right
     */
    <R2> Either<L, R2> pinLeft();

    /**
     * If this is a Right, returns the receiver.
     *
     * <p>If this is a Left, an exception is thrown.
     *
     * @return if this is a Right, the same object unchanged
     * @throws UnsupportedOperationException if this is a Left
     */
    <L2> Either<L2, R> pinRight();

    /**
     * If this is a Left, returns the result of applying {@code leftMapper} to the value.
     *
     * <p>If this is a Right, returns the result of applying {@code rightMapper} to the value.
     *
     * @param leftMapper the function to apply if this is a Left
     * @param rightMapper the function to apply if this is a Right
     * @param <U> the result type of both {@code leftMapper} and {@code rightMapper}
     * @return the result of applying either {@code leftMapper} or {@code rightMapper}
     */
    <U> U fold(
            Function<? super L, ? extends U> leftMapper,
            Function<? super R, ? extends U> rightMapper);

    /**
     * If this is a Left, performs the {@code leftAction} with the value.
     *
     * <p>If this is a Right, performs the {@code rightAction} with the value.
     *
     * @param leftAction action to run if this is a Left
     * @param rightAction action to run if this is a Right
     */
    void ifLeftOrElse(
            Consumer<? super L> leftAction,
            Consumer<? super R> rightAction);

    /**
     * If this is a Left, returns the value.
     *
     * <p>If this is a Right, throws the result of applying {@code exceptionSupplier} to the value.
     *
     * @param exceptionSupplier exception supplier
     * @return the value, if this is a Left
     * @throws RuntimeException the result of applying {@code exceptionSupplier}, if this is a Right
     */
    L getLeftOrElseThrow(
            Function<? super R, ? extends RuntimeException> exceptionSupplier);

    /**
     * If this is a Left, returns the value.
     *
     * <p>If this is a Right, throws an exception.
     *
     * @return the value, if this is a Left
     * @throws UnsupportedOperationException if this is a Right
     */
    L getLeftOrElseThrow();

    /**
     * If this is a Right, returns the value.
     *
     * <p>If this is a Left, throws the result of applying {@code exceptionSupplier} to the value.
     *
     * @param exceptionSupplier exception supplier
     * @return the value, if this is a Right
     * @throws RuntimeException the result of applying {@code exceptionSupplier} to the value, if this is a Left
     */
    R getRightOrElseThrow(
            Function<? super L, ? extends RuntimeException> exceptionSupplier);

    /**
     * If this is a Right, returns the value.
     *
     * <p>If this is a Left, throws an exception.
     *
     * @return the value, if this is a Right
     * @throws UnsupportedOperationException if this is a Left
     */
    R getRightOrElseThrow();

    /**
     * Returns {@code true} if this is a Left, otherwise {@code false}.
     *
     * @return {@code true} if this is a Left, otherwise {@code false}
     */
    boolean isLeft();

    /**
     * Returns {@code true} if this is a Right, otherwise {@code false}.
     *
     * @return {@code true} if this is a Right, otherwise {@code false}
     */
    boolean isRight();

    /**
     * If this is a Left, returns an optional containing the value,
     * or, if the value is {@code null}, an empty optional.
     *
     * <p>If this is a Right, returns an empty optional.
     *
     * <p>Note, if you need the value,
     * {@code getLeftOrElseThrow()} is more efficient.
     *
     * @return an optional containing the value
     * or an empty optional
     */
    Optional<L> getLeft();

    /**
     * If this is a Right, returns an optional containing the value,
     * or, if the value is {@code null}, an empty optional.
     *
     * <p>If this is a Left, returns an empty optional.
     *
     * <p>Note, if you need the value,
     * {@code getRightOrElseThrow()} is more efficient.
     *
     * @return an optional containing the value
     * or an empty optional
     */
    Optional<R> getRight();
}
