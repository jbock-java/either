package io.jbock.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Container for a Right-Value.
 *
 * @param <L> any arbitrary type
 * @param <R> value type
 */
public record Right<L, R>(R value) implements Either<L, R> {

    @Override
    public <R2> Either<L, R2> mapIdentity(Identity<R, ? extends R2> identity) {
        @SuppressWarnings("unchecked")
        Either<L, R2> result = (Either<L, R2>) this;
        return result;
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
    public boolean isRight() {
        return true;
    }

    @Override
    public Optional<R> getRight() {
        return Optional.ofNullable(value);
    }

    @Override
    public <R2> Either<L, R2> map(Function<? super R, ? extends R2> mapper) {
        return new Right<>(mapper.apply(value));
    }

    @Override
    public Either<L, R> filter(Function<? super R, Optional<? extends L>> filter) {
        Optional<? extends L> test = filter.apply(value);
        if (test.isEmpty()) {
            return this;
        }
        return new Left<>(test.orElseThrow());
    }

    @Override
    public <R2> Either<L, R2> filterMap(Function<? super R, ? extends Either<L, ? extends R2>> mapper) {
        @SuppressWarnings("unchecked")
        Either<L, R2> result = (Either<L, R2>) mapper.apply(value);
        return result;
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
