package io.jbock.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Internal implementation of {@link Either#toValidListAll()}.
 *
 * @param <L> the type of the LHS values in the stream
 * @param <R> the type of the RHS values in the stream
 */
final class ValidatingCollectorAll<L, R> implements Collector<Either<? extends L, ? extends R>, ValidatingCollectorAll.Acc<L, R>, Either<List<L>, List<R>>> {

    static final class Acc<L, R> {

        private final List<L> left = new ArrayList<>();
        private final List<R> right = new ArrayList<>();

        void accumulate(Either<? extends L, ? extends R> either) {
            if (!left.isEmpty()) {
                either.ifLeftOrElse(left::add, r -> {
                });
            } else {
                either.ifLeftOrElse(left::add, right::add);
            }
        }

        Acc<L, R> combine(Acc<L, R> other) {
            if (!left.isEmpty()) {
                left.addAll(other.left);
                return this;
            }
            if (!other.left.isEmpty()) {
                return other;
            }
            right.addAll(other.right);
            return this;
        }

        Either<List<L>, List<R>> finish() {
            if (!left.isEmpty()) {
                return Either.left(left);
            } else {
                return Either.right(right);
            }
        }
    }

    @Override
    public Supplier<Acc<L, R>> supplier() {
        return Acc::new;
    }

    @Override
    public BiConsumer<Acc<L, R>, Either<? extends L, ? extends R>> accumulator() {
        return Acc::accumulate;
    }

    @Override
    public BinaryOperator<Acc<L, R>> combiner() {
        return Acc::combine;
    }

    @Override
    public Function<Acc<L, R>, Either<List<L>, List<R>>> finisher() {
        return Acc::finish;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}
