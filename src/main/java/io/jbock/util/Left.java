package io.jbock.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Container for a Left-Value.
 *
 * @param <L> value type
 * @param <R> any arbitrary type
 */
public record Left<L, R>(L value) implements Either<L, R> {

    @Override
    public Optional<L> getLeft() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public boolean isRight() {
        return false;
    }

    @Override
    public Optional<R> getRight() {
        return Optional.empty();
    }

    @Override
    public <R2> Either<L, R2> map(Function<? super R, ? extends R2> mapper) {
        @SuppressWarnings("unchecked")
        Either<L, R2> result = (Either<L, R2>) this;
        return result;
    }

    @Override
    public Either<L, R> filter(Function<? super R, Optional<? extends L>> filter) {
        return this;
    }

    @Override
    public <R2> Either<L, R2> filterMap(Function<? super R, ? extends Either<L, ? extends R2>> mapper) {
        return pinLeft();
    }

    @Override
    public <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> mapper) {
        return new Left<>(mapper.apply(value));
    }

    @Override
    public <R2> Either<L, R2> pinLeft() {
        @SuppressWarnings("unchecked")
        Left<L, R2> result = (Left<L, R2>) this;
        return result;
    }

    @Override
    public <L2> Either<L2, R> pinRight() {
        throw new UnsupportedOperationException("not a Right");
    }

    @Override
    public <U> U fold(
            Function<? super L, ? extends U> leftMapper,
            Function<? super R, ? extends U> rightMapper) {
        return leftMapper.apply(value);
    }

    @Override
    public void ifLeftOrElse(Consumer<? super L> leftAction, Consumer<? super R> rightAction) {
        leftAction.accept(value);
    }

    @Override
    public L getLeftOrElseThrow(Function<? super R, ? extends RuntimeException> exceptionSupplier) {
        return value;
    }

    @Override
    public L getLeftOrElseThrow() {
        return value;
    }

    @Override
    public R getRightOrElseThrow(Function<? super L, ? extends RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.apply(value);
    }

    @Override
    public R getRightOrElseThrow() {
        throw new UnsupportedOperationException("not a Left");
    }
}
