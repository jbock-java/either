package io.jbock.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

final class Right<L, R> extends Either<L, R> {

    private final R value;

    private Right(R value) {
        this.value = Objects.requireNonNull(value);
    }

    static <L, R> Right<L, R> create(R value) {
        return new Right<>(value);
    }

    @Override
    public LeftOptional<L> getLeft() {
        return LeftOptional.empty();
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
        return create(mapper.apply(value));
    }

    @Override
    public <R2> Either<L, R2> flatMap(Function<? super R, ? extends Either<? extends L, ? extends R2>> mapper) {
        return narrow(mapper.apply(value));
    }

    @Override
    public Either<L, R> filter(Function<? super R, LeftOptional<? extends L>> predicate) {
        LeftOptional<? extends L> test = predicate.apply(value);
        if (test.isEmpty()) {
            return same();
        }
        return Left.create(test.orElseThrow());
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
    public <X extends Throwable> R orElseThrow(Function<? super L, ? extends X> exceptionSupplier) throws X {
        return value;
    }

    @Override
    public <U> U fold(
            Function<? super L, ? extends U> leftMapper,
            Function<? super R, ? extends U> rightMapper) {
        return rightMapper.apply(value);
    }

    @Override
    public void ifPresentOrElse(Consumer<? super L> leftAction, Consumer<? super R> rightAction) {
        rightAction.accept(value);
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
