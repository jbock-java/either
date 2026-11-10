package io.jbock.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Internal implementation of a Right-Either.
 *
 * @param <L> any arbitrary type
 * @param <R> value type
 */
record Right<L, R>(R value) implements Either<L, R> {

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
    public Either<L, R> filter(Function<? super R, Optional<? extends L>> predicate) {
        Optional<? extends L> test = predicate.apply(value);
        if (test.isEmpty()) {
            return this;
        }
        return new Left<>(test.orElseThrow());
    }

    @Override
    public <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> mapper) {
        @SuppressWarnings("unchecked")
        Either<L2, R> result = (Either<L2, R>) this;
        return result;
    }

    @Override
    public <R2> Either<L, R2> pinLeft() {
        throw new UnsupportedOperationException("not a Left");
    }

    @Override
    public <L2> Either<L2, R> pinRight() {
        @SuppressWarnings("unchecked")
        Right<L2, R> result = (Right<L2, R>) this;
        return result;
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
    public L getLeftOrElseThrow(Function<? super R, ? extends RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.apply(value);
    }

    @Override
    public L getLeftOrElseThrow() {
        throw new UnsupportedOperationException("not a Right");
    }

    @Override
    public R getRightOrElseThrow(Function<? super L, ? extends RuntimeException> exceptionSupplier) {
        return value;
    }

    @Override
    public R getRightOrElseThrow() {
        return value;
    }
}
