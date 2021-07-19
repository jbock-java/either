package io.jbock.util;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EitherTest {

    @Test
    void testEquals() {
        new EqualsTester()
                .addEqualityGroup(Either.right("1"), Either.right("1"))
                .addEqualityGroup(Either.right("2"), Either.right("2"))
                .testEquals();
        new EqualsTester()
                .addEqualityGroup(Either.left("1"), Either.left("1"))
                .addEqualityGroup(Either.left("2"), Either.left("2"))
                .testEquals();
    }

    @Test
    void testToString() {
        assertEquals("Right[1]", Either.right("1").toString());
        assertEquals("Left[1]", Either.left("1").toString());
    }

    @Test
    void testGetLeft() {
        Either<String, ?> either = Either.left("1");
        assertTrue(either.isLeft());
        assertFalse(either.isRight());
        assertEquals(Optional.of("1"), either.getLeft());
        assertEquals(Optional.empty(), either.getRight());
    }

    @Test
    void testGetRight() {
        Either<?, String> either = Either.right("1");
        assertTrue(either.isRight());
        assertFalse(either.isLeft());
        assertEquals(Optional.of("1"), either.getRight());
        assertEquals(Optional.empty(), either.getLeft());
    }

    @Test
    void testMap() {
        Either<String, String> left = Either.left("1");
        assertSame(left, left.map(s -> "A")); // Left is unchanged
        Either<?, String> right = Either.right("1");
        assertEquals(Either.right(1), right.map(Integer::parseInt));
    }

    @Test
    void testFlatMap() {
        Either<Integer, String> left = Either.left(2);
        assertSame(left, left.flatMap(s -> Either.right("1"))); // Left is unchanged
        assertSame(left, left.flatMap(s -> Either.left(1))); // Left is unchanged
        Either<Integer, String> right = Either.right("1");
        assertEquals(Either.right(11), right.flatMap(s -> Either.right(11)));
        assertEquals(Either.left(11), right.flatMap(s -> Either.left(11)));
    }

    @Test
    void testMapLeft() {
        Either<String, ?> left = Either.left("1");
        assertEquals(Either.left(1), left.mapLeft(Integer::parseInt));
        Either<String, String> right = Either.right("1");
        assertSame(right, right.mapLeft(s -> "A")); // Right is unchanged
    }

    @Test
    void testFlatMapLeft() {
        Either<String, Integer> left = Either.left("1");
        assertEquals(Either.left(11), left.flatMapLeft(s -> Either.left(11)));
        assertEquals(Either.right(11), left.flatMapLeft(s -> Either.right(11)));
        Either<String, Integer> right = Either.right(2);
        assertSame(right, right.flatMapLeft(s -> Either.left("1"))); // Right is unchanged
        assertSame(right, right.flatMapLeft(s -> Either.right(1))); // Right is unchanged
    }

    @Test
    void testFilter() {
        Either<String, ?> left = Either.left("1");
        assertSame(left, left.filter(r -> Optional.of("2"))); // Left is unchanged
        assertSame(left, left.filter(r -> Optional.empty())); // Left is unchanged
        Either<String, String> right = Either.right("1");
        assertEquals(Either.left("2"), right.filter(r -> Optional.of("2")));
        assertEquals(right, right.filter(r -> Optional.empty()));
    }

    @Test
    void testFilterLeft() {
        Either<?, String> right = Either.right("1");
        assertSame(right, right.filterLeft(r -> Optional.of("2"))); // Right is unchanged
        assertSame(right, right.filterLeft(r -> Optional.empty())); // Right is unchanged
        Either<String, String> left = Either.left("1");
        assertEquals(Either.right("2"), left.filterLeft(r -> Optional.of("2")));
        assertEquals(left, left.filterLeft(r -> Optional.empty()));
    }

    @Test
    void testIfLeftOrElse() {
        String[] output = {"1"};
        Either<Integer, Integer> left = Either.left(1);
        left.ifLeftOrElse(l -> output[0] = "L", r -> output[0] = "R");
        assertEquals("L", output[0]);
        Either<Integer, Integer> right = Either.right(1);
        right.ifLeftOrElse(l -> output[0] = "L", r -> output[0] = "R");
        assertEquals("R", output[0]);
    }

    @Test
    void testFold() {
        Either<String, Integer> left = Either.left("1");
        assertEquals("1", left.fold(Objects::toString, Objects::toString));
        Either<String, Integer> right = Either.right(2);
        assertEquals("2", right.fold(Objects::toString, Objects::toString));
    }

    @Test
    void testOrElseThrow() {
        Either<String, ?> left = Either.left("1");
        IOException x = assertThrows(IOException.class, () -> left.orElseThrow(IOException::new));
        assertEquals("1", x.getMessage());
        Either<String, String> right = Either.right("2");
        assertEquals("2", right.orElseThrow(IllegalArgumentException::new));
    }

    @Test
    void testOptionalList() {
        assertEquals(Optional.empty(), Either.optionalList(List.of()));
        assertEquals(Optional.of(List.of(1)), Either.optionalList(List.of(1)));
        assertEquals(Optional.of(List.of("1", "2")), Either.optionalList(List.of("1", "2")));
    }
}
