package io.jbock.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Internal implementation of a Right-Either.
 *
 * @param <L> the type of the LHS value
 * @param <R> the type of the RHS value
 */
final class Right<L, R> extends Either<L, R> {

    private final R value;

    Right(R value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public Optional<L> getLeft() {
        return Optional.empty();
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public Optional<R> getRight() {
        return Optional.of(value);
    }

    @Override
    public <R2> Either<L, R2> map(Function<? super R, ? extends R2> mapper) {
        return new Right<>(mapper.apply(value));
    }

    @Override
    public <R2> Either<L, R2> flatMap(Function<? super R, ? extends Either<? extends L, ? extends R2>> mapper) {
        return narrow(mapper.apply(value));
    }

    @Override
    public Either<L, R> filter(Function<? super R, Optional<? extends L>> predicate) {
        Optional<? extends L> test = predicate.apply(value);
        if (test.isEmpty()) {
            return same();
        }
        return new Left<>(test.orElseThrow());
    }

    @Override
    public <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> mapper) {
        return same();
    }

    @Override
    public <L2> Either<L2, R> flatMapLeft(Function<? super L, ? extends Either<? extends L2, ? extends R>> mapper) {
        return same();
    }

    @Override
    public Either<L, R> filterLeft(Function<? super L, Optional<? extends R>> predicate) {
        return same();
    }

    @Override
    public <U> U fold(
            Function<? super L, ? extends U> leftMapper,
            Function<? super R, ? extends U> rightMapper) {
        return rightMapper.apply(value);
    }

    @Override
    public void ifLeftOrElse(Consumer<? super L> leftAction, Consumer<? super R> rightAction) {
        rightAction.accept(value);
    }

    @Override
    public <X extends Throwable> R orElseThrow(Function<? super L, ? extends X> exceptionSupplier) {
        return value;
    }

    @Override
    public String toString() {
        return String.format("Right[%s]", value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Right)) {
            return false;
        }

        Right<?, ?> other = (Right<?, ?>) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @SuppressWarnings("unchecked")
    private <L2> Either<L2, R> same() {
        // equivalently: return new Right<>(value);
        return (Either<L2, R>) this;
    }
}
