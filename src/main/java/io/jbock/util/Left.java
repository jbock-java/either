package io.jbock.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

final class Left<L, R> extends Either<L, R> {

    private final L value;

    private Left(L value) {
        this.value = Objects.requireNonNull(value);
    }

    static <L, R> Left<L, R> create(L value) {
        return new Left<>(value);
    }

    @Override
    public LeftOptional<L> getLeft() {
        return LeftOptional.of(value);
    }

    @Override
    public boolean isLeft() {
        return true;
    }

    @Override
    public Optional<R> getRight() {
        return Optional.empty();
    }

    @Override
    public <R2> Either<L, R2> map(Function<? super R, ? extends R2> mapper) {
        return same();
    }

    @Override
    public <R2> Either<L, R2> flatMap(Function<? super R, ? extends Either<? extends L, ? extends R2>> mapper) {
        return same();
    }

    @Override
    public Either<L, R> filter(Function<? super R, LeftOptional<? extends L>> predicate) {
        return same();
    }

    @Override
    public <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> mapper) {
        return create(mapper.apply(value));
    }

    @Override
    public <L2> Either<L2, R> flatMapLeft(Function<? super L, ? extends Either<? extends L2, ? extends R>> mapper) {
        return narrow(mapper.apply(value));
    }

    @Override
    public Either<L, R> filterLeft(Function<? super L, Optional<? extends R>> predicate) {
        Optional<? extends R> test = predicate.apply(value);
        if (test.isEmpty()) {
            return same();
        }
        return Right.create(test.orElseThrow());
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
    public String toString() {
        return String.format("Left[%s]", value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Left)) {
            return false;
        }

        Left<?, ?> other = (Left<?, ?>) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @SuppressWarnings("unchecked")
    private <R2> Either<L, R2> same() {
        // equivalently: return new Left<>(value);
        return (Either<L, R2>) this;
    }
}
