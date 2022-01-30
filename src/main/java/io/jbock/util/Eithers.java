package io.jbock.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This class contains static utility methods related to
 * the {@link Either} type.
 */
public final class Eithers {

    static final Set<Collector.Characteristics> CH_NOID = Set.of();

    private Eithers() {
    }

    /**
     * Simple implementation class for a collector with characteristic {@link #CH_NOID}.
     *
     * @param <T> the type of elements to be collected
     * @param <R> the type of the result
     */
    private static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        final Supplier<A> supplier;
        final BiConsumer<A, T> accumulator;
        final BinaryOperator<A> combiner;
        final Function<A, R> finisher;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A, R> finisher) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return CH_NOID;
        }
    }

    private static abstract class BasicAcc<L, R> {
        List<R> right;

        final List<R> right() {
            if (right == null) {
                right = new ArrayList<>();
            }
            return right;
        }
    }

    // visible for testing
    static final class Acc<L, R> extends BasicAcc<L, R> {
        private L left;
    }

    // visible for testing
    static final class AccAll<L, R> extends BasicAcc<L, R> {
        private List<L> left;

        void addLeft(L newLeft) {
            if (left == null) {
                left = new ArrayList<>();
            }
            left.add(newLeft);
        }

        private List<L> left() {
            if (left == null) {
                left = new ArrayList<>();
            }
            return left;
        }

        boolean isLeftEmpty() {
            return left == null || left().isEmpty();
        }
    }

    /**
     * Returns a {@code Collector} that accumulates the input elements into
     * a Right containing all values in the original order,
     * but only if there are no Left instances in the stream.
     * If the stream does contain a Left instance, it discards the Right instances and
     * accumulates a Left instance, which contains the first LHS value in the stream,
     * in encounter order.
     *
     * @param <L> the type of the LHS values in the stream
     * @param <R> the type of the RHS values in the stream
     * @return a {@code Collector} which collects all the input elements into
     *         a Right containing all RHS values in the stream, or,
     *         if an LHS value exists, a Left containing the first LHS value
     */
    public static <L, R> Collector<Either<? extends L, ? extends R>, ?, Either<L, List<R>>> toValidList() {

        BiConsumer<Acc<L, R>, Either<? extends L, ? extends R>> accumulate = (acc, either) -> {
            if (acc.left != null) {
                return;
            }
            either.ifLeftOrElse(
                    left -> acc.left = left,
                    acc.right()::add);
        };

        BinaryOperator<Acc<L, R>> combine = (acc, other) -> {
            if (acc.left != null) {
                return acc;
            }
            if (other.left != null) {
                return other;
            }
            acc.right().addAll(other.right());
            return acc;
        };

        Function<Acc<L, R>, Either<L, List<R>>> finish = acc -> {
            if (acc.left != null) {
                return Either.left(acc.left);
            }
            return Either.right(acc.right());
        };

        return new CollectorImpl<>(Acc::new, accumulate, combine, finish);
    }

    /**
     * Returns a {@code Collector} that accumulates the input elements into
     * a Right containing all values in the original order,
     * but only if there are no Left instances in the stream.
     * If the stream does contain a Left instance, it discards the Right instances and
     * accumulates a Left containing only the LHS values,
     * in encounter order.
     *
     * @param <L> the type of the LHS values in the stream
     * @param <R> the type of the RHS values in the stream
     * @return a {@code Collector} which collects all the input elements into
     *         a Right containing all RHS values in the stream,
     *         or, if an LHS value exists, a Left containing a nonempty list
     *         of all LHS values in the stream
     */
    public static <L, R> Collector<Either<? extends L, ? extends R>, ?, Either<List<L>, List<R>>> toValidListAll() {

        BiConsumer<AccAll<L, R>, Either<? extends L, ? extends R>> accumulate = (acc, either) ->
                either.ifLeftOrElse(acc::addLeft, acc.right()::add);

        BinaryOperator<AccAll<L, R>> combine = (acc, other) -> {
            if (!acc.isLeftEmpty()) {
                acc.left().addAll(other.left());
                return acc;
            }
            if (!other.isLeftEmpty()) {
                return other;
            }
            acc.right().addAll(other.right());
            return acc;
        };

        Function<AccAll<L, R>, Either<List<L>, List<R>>> finish = acc -> {
            if (!acc.isLeftEmpty()) {
                return Either.left(acc.left());
            } else {
                return Either.right(acc.right());
            }
        };

        return new CollectorImpl<>(AccAll::new, accumulate, combine, finish);
    }

    /**
     * Returns a {@code Collector} that accumulates the input elements into
     * a new {@code List}. There are no guarantees on the type, mutability,
     * serializability, or thread-safety of the {@code List} returned.
     * The resulting list is wrapped in an {@code Optional},
     * which is empty if and only if the list is empty.
     *
     * @see #optionalList(List)
     * @param <T> the type of the input elements
     * @return a list of the RHS values in the stream,
     *         or, if an LHS value exists, a nonempty list of all LHS values
     */
    public static <T> Collector<T, ?, Optional<List<T>>> toOptionalList() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                Eithers::optionalList);
    }

    /**
     * If the provided list is empty, returns an empty {@link Optional}.
     * Otherwise, returns an {@code Optional} containing the nonempty
     * input list.
     *
     * <p>Note: The resulting {@code Optional} might be used in a
     * {@link Either#filter(Function) filter} or
     * {@link Either#filterLeft(Function) filterLeft} operation.
     *
     * @see #toOptionalList()
     * @param values a list of objects
     * @param <T> the type of the members of {@code values}
     * @return an {@code Optional} which is either empty, or
     *         contains a nonempty list
     */
    public static <T> Optional<List<T>> optionalList(List<? extends T> values) {
        if (values.isEmpty()) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        List<T> result = (List<T>) values;
        return Optional.of(result);
    }
}
