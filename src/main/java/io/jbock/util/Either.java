package io.jbock.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * A class that acts as a container for a value of one of two types. An Either
 * can be either be a "Left", containing a LHS value or a "Right" containing a RHS value,
 * but it cannot be "neither" or "both".
 *
 * <p>An Either can be used to express a success or failure case. By convention,
 * a Right contains the result of a successful computation,
 * and a Left contains some kind of failure object.
 *
 * @param <L> the type of the LHS value
 * @param <R> the type of the RHS value
 */
public abstract class Either<L, R> {

    Either() {
    }

    /**
     * Returns a Left containing the given non-{@code null} LHS value.
     *
     * @param value the LHS value
     * @param <L> the type of the LHS value
     * @param <R> an arbitrary RHS type
     * @return a Left containing the LHS value
     * @throws NullPointerException if value is {@code null}
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
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
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    /**
     * Returns a collector that accumulates a Right containing all values in the original order,
     * if there are no Left instances in the stream.
     * If the stream contains a Left, it accumulates a Left containing the first LHS value in the stream.
     *
     * @param <L> the LHS type
     * @param <R> the RHS type
     * @return a Right containing all RHS values in the stream,
     *         or, if an LHS value exists, a Left containing the first such value
     */
    public static <L, R> Collector<Either<? extends L, ? extends R>, ?, Either<L, List<R>>> toValidList() {
        return new ValidatingCollector<>();
    }

    /**
     * Returns a collector that accumulates a Right containing all values in the original order,
     * if there are no Left instances in the stream.
     * If the stream contains a Left, it accumulates a Left containing all LHS values in the stream,
     * in the original order.
     *
     * @param <L> the LHS type
     * @param <R> the RHS type
     * @return a list of the RHS values in the stream,
     *         or, if an LHS value exists, a nonempty list of all LHS values
     */
    public static <L, R> Collector<Either<? extends L, ? extends R>, ?, Either<List<L>, List<R>>> toValidListAll() {
        return new ValidatingCollectorAll<>();
    }

    /**
     * If the provided list is empty, returns an empty {@link Optional}.
     * Otherwise, returns an {@code Optional} containing the list.
     *
     * @param failures a failures
     * @param <L> the type of the members of the failures
     * @return an {@code Optional} which is empty if and only if {@code failures}
     *         is empty
     */
    public static <L> Optional<List<L>> asLeftOptional(List<? extends L> failures) {
        if (failures.isEmpty()) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        List<L> result = (List<L>) failures;
        return Optional.of(result);
    }

    /**
     * If this is a Right, returns a Right containing the result of applying
     * the mapper function to the RHS value.
     * Otherwise returns a Left containing the LHS value.
     *
     * @param mapper the function to apply to the RHS value, if this is a Right
     * @param <R2> the new RHS type
     * @return an equivalent instance if this is a Left, otherwise a Right containing
     *         the result of applying {@code mapper} to the RHS value
     * @throws NullPointerException if the {@code mapper} returns a {@code null} result
     */
    public abstract <R2> Either<L, R2> map(
            Function<? super R, ? extends R2> mapper);

    /**
     * If this is a Right, returns the result of applying the mapper function to the RHS value.
     * Otherwise returns a Left containing the LHS value.
     *
     * @param mapper a mapper function
     * @param <R2> the new RHS type
     * @return an equivalent instance if this is a Left, otherwise the result of
     *         applying {@code mapper} to the RHS value
     */
    public abstract <R2> Either<L, R2> flatMap(
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
    public abstract Either<L, R> filter(
            Function<? super R, Optional<? extends L>> predicate);

    /**
     * If this is a Left, returns a Left containing the result of applying the mapper function to the LHS value.
     * Otherwise returns a Right containing the RHS value.
     *
     * @param mapper the function to apply to the LHS value
     * @param <L2> the new LHS type
     * @return an equivalent instance if this is a Right, otherwise a Left containing
     *         the result of applying {@code mapper} to the LHS value
     * @throws NullPointerException if the {@code mapper} returns a {@code null} result
     */
    public abstract <L2> Either<L2, R> mapLeft(
            Function<? super L, ? extends L2> mapper);

    /**
     * If this is a Left, returns the result of applying the mapper function to the LHS value.
     * Otherwise returns a Right containing the RHS value.
     *
     * @param mapper a mapper function
     * @param <L2> the new LHS type
     * @return an equivalent instance if this is a Right, otherwise the result of
     *         applying {@code mapper} to the LHS value
     */
    public abstract <L2> Either<L2, R> flatMapLeft(
            Function<? super L, ? extends Either<? extends L2, ? extends R>> mapper);

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
    public abstract Either<L, R> filterLeft(
            Function<? super L, Optional<? extends R>> predicate);

    /**
     * If this is a Left, returns the result of applying the {@code leftMapper} to the LHS value.
     * Otherwise returns the result of applying the {@code rightMapper} to the RHS value.
     *
     * @param leftMapper the function to apply if this is a Left
     * @param rightMapper the function to apply if this is a Right
     * @param <U> the result type of both {@code leftMapper} and {@code rightMapper}
     * @return the result of applying either {@code leftMapper} or {@code rightMapper}
     */
    public abstract <U> U fold(
            Function<? super L, ? extends U> leftMapper,
            Function<? super R, ? extends U> rightMapper);

    /**
     * If this is a Left, performs the {@code leftAction} with the LHS value.
     * Otherwise performs the {@code rightAction} with the RHS value.
     *
     * @param leftAction action to run if this is a Left
     * @param rightAction action to run if this is a Right
     */
    public abstract void ifLeftOrElse(
            Consumer<? super L> leftAction,
            Consumer<? super R> rightAction);

    /**
     * If this is a Right, returns the RHS value.
     * Otherwise throws an exception produced by the exception supplying function.
     *
     * @param exceptionSupplier exception supplying function
     * @param <X> type of the exception
     * @return the RHS value, if this is a Right
     * @throws X the result of applying {@code exceptionSupplier} to the LHS value, if this is a Left
     */
    public abstract <X extends Throwable> R orElseThrow(
            Function<? super L, ? extends X> exceptionSupplier) throws X;

    /**
     * Returns {@code true} if this is a Left, otherwise {@code false}.
     *
     * @return {@code true} if this is a Left, otherwise {@code false}
     */
    public abstract boolean isLeft();

    /**
     * Returns {@code true} if this is a Right, otherwise {@code false}.
     *
     * @return {@code true} if this is a Right, otherwise {@code false}
     */
    public final boolean isRight() {
        return !isLeft();
    }

    /**
     * If this is a Left, returns an {@code Optional} containing the LHS value.
     * Otherwise returns an empty {@code Optional}.
     *
     * @return the LHS value if this is a Left, otherwise an empty {@code Optional}
     */
    public abstract Optional<L> getLeft();

    /**
     * If this is a Right, returns an {@code Optional} containing the RHS value.
     * Otherwise returns an empty {@code Optional}.
     *
     * @return the RHS value if this is a Right, otherwise an empty {@code Optional}
     */
    public abstract Optional<R> getRight();

    /**
     * Returns a string representation of this {@code Either}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @return the string representation of this instance
     */
    @Override
    public abstract String toString();
}
