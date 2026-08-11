package io.jbock.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A class that acts as a container for a value of one of two types. An Either
 * can be either be a "Left", containing an LHS value or a "Right" containing an RHS value,
 * but it cannot be "neither" nor "both".
 *
 * <p>An Either can be used to express a success or failure case. By convention,
 * a Right contains the result of a successful computation,
 * and a Left contains some kind of failure object.
 *
 * @param <L> the type of the LHS value
 * @param <R> the type of the RHS value
 */
public sealed interface Either<L, R> permits Left, Right {

    /**
     * Returns a Left containing the given non-{@code null} LHS value.
     *
     * @param value the LHS value
     * @param <L> the type of the LHS value
     * @param <R> an arbitrary RHS type
     * @return a Left containing the LHS value
     * @throws NullPointerException if value is {@code null}
     */
    static <L, R> Either<L, R> left(L value) {
        return new Left<>(Objects.requireNonNull(value));
    }

    /**
     * Returns a Right containing the given non-{@code null} RHS value.
     *
     * @param value the RHS value
     * @param <L> an arbitrary LHS type
     * @param <R> the type of the RHS value
     * @return a Right containing the RHS value
     * @throws NullPointerException if value is {@code null}
     */
    static <L, R> Either<L, R> right(R value) {
        return new Right<>(Objects.requireNonNull(value));
    }

    /**
     * If this is a Right, returns a Right containing the result of applying
     * the mapper function to the RHS value.
     * Otherwise, returns a Left containing the LHS value.
     *
     * @param mapper the function to apply to the RHS value, if this is a Right
     * @param <R2> the new RHS type
     * @return an equivalent instance if this is a Left, otherwise a Right containing
     *         the result of applying {@code mapper} to the RHS value
     * @throws NullPointerException if the {@code mapper} returns a {@code null} result
     */
    <R2> Either<L, R2> map(
            Function<? super R, ? extends R2> mapper);

    /**
     * If this is a Right, returns the result of applying the mapper function to the RHS value.
     * Otherwise, returns a Left containing the LHS value.
     *
     * @param mapper a mapper function
     * @param <R2> the new RHS type
     * @return an equivalent instance if this is a Left, otherwise the result of
     *         applying {@code mapper} to the RHS value
     */
    <R2> Either<L, R2> flatMap(
            Function<? super R, ? extends Either<? extends L, ? extends R2>> mapper);

    /**
     * If this is a Left, returns a Left containing the LHS value.
     * If this is a Right, applies the predicate function to the RHS value.
     * If the predicate function returns an empty result,
     * returns a Right containing the RHS value.
     * If the result is not empty, returns a Left containing the result.
     *
     * @param predicate a function that acts as a filter predicate
     * @return filter result
     */
    Either<L, R> filter(
            Function<? super R, Optional<? extends L>> predicate);

    /**
     * If this is a Left, returns a Left containing the result of applying the mapper function to the LHS value.
     * Otherwise, returns a Right containing the RHS value.
     *
     * @param mapper the function to apply to the LHS value
     * @param <L2> the new LHS type
     * @return an equivalent instance if this is a Right, otherwise a Left containing
     *         the result of applying {@code mapper} to the LHS value
     * @throws NullPointerException if the {@code mapper} returns a {@code null} result
     */
    <L2> Either<L2, R> mapLeft(
            Function<? super L, ? extends L2> mapper);

    /**
     * If this is a Left, returns the result of applying the mapper function to the LHS value.
     * Otherwise, returns a Right containing the RHS value.
     *
     * @param mapper a mapper function
     * @param <L2> the new LHS type
     * @return an equivalent instance if this is a Right, otherwise the result of
     *         applying {@code mapper} to the LHS value
     */
    <L2> Either<L2, R> flatMapLeft(
            Function<? super L, ? extends Either<? extends L2, ? extends R>> mapper);

    /**
     * Returns the receiver object unchanged. This method should only be invoked
     * if this is provably a Left.
     *
     * @return the same object
     * @throws UnsupportedOperationException if this is a Right
     */
    <R2> Either<L, R2> pinLeft();

    /**
     * Returns the receiver object unchanged. This method should only be invoked
     * if this is provably a Right.
     *
     * @return the same object
     * @throws UnsupportedOperationException if this is a Left
     */
    <L2> Either<L2, R> pinRight();

    /**
     * If this is a Right, returns a Right containing the RHS value.
     * If this is a Left, applies the predicate function to the LHS value.
     * If the predicate function returns an empty result,
     * returns a Left containing the LHS value.
     * If the result is not empty, returns a Right containing the result.
     *
     * @param predicate a function that acts as a filter predicate
     * @return filter result
     */
    Either<L, R> filterLeft(
            Function<? super L, Optional<? extends R>> predicate);

    /**
     * If this is a Left, returns the result of applying the {@code leftMapper} to the LHS value.
     * Otherwise, returns the result of applying the {@code rightMapper} to the RHS value.
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
     * If this is a Left, performs the {@code leftAction} with the LHS value.
     * Otherwise, performs the {@code rightAction} with the RHS value.
     *
     * @param leftAction action to run if this is a Left
     * @param rightAction action to run if this is a Right
     */
    void ifLeftOrElse(
            Consumer<? super L> leftAction,
            Consumer<? super R> rightAction);

    /**
     * If this is a Right, returns the RHS value.
     * Otherwise, throws an exception produced by the exception supplying function.
     *
     * @param exceptionSupplier exception supplying function
     * @param <X> type of the exception
     * @return the RHS value, if this is a Right
     * @throws X the result of applying {@code exceptionSupplier} to the LHS value, if this is a Left
     */
    <X extends Throwable> R orElseThrow(
            Function<? super L, ? extends X> exceptionSupplier) throws X;

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
    default boolean isRight() {
        return !isLeft();
    }

    /**
     * If this is a Left, returns an {@code Optional} containing the LHS value.
     * Otherwise, returns an empty {@code Optional}.
     *
     * @return the LHS value if this is a Left, otherwise an empty {@code Optional}
     */
    Optional<L> getLeft();

    /**
     * If this is a Right, returns an {@code Optional} containing the RHS value.
     * Otherwise, returns an empty {@code Optional}.
     *
     * @return the RHS value if this is a Right, otherwise an empty {@code Optional}
     */
    Optional<R> getRight();
}
