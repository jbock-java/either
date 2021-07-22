package io.jbock.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This class contains static utility methods related to
 * the {@link Either} type.
 */
public final class Eithers {

    private Eithers() {
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
        return new ValidatingCollector<>();
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
        return new ValidatingCollectorAll<>();
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
