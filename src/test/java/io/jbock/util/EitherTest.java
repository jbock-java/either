package io.jbock.util;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EitherTest {

    @Test
    void testMapIdentity() {
        // bounded wildcard
        Either<String, ? extends String> left = Either.left("left");
        // back to normal
        Either<String, String> l = left.mapIdentity(Either.identity());
        assertSame(left, l);
        // bounded wildcard
        Either<String, ? extends String> right = Either.right("normal");
        // back to normal
        Either<String, String> r = right.mapIdentity(Either.identity());
        assertSame(right, r);
    }

    @Test
    void testEqualsAndHashCode() {
        assertEquals(Either.right("1"), Either.right("1"));
        assertEquals(Either.right("1").hashCode(), Either.right("1").hashCode());

        assertNotEquals(Either.right("1"), Either.right("2"));
        assertNotEquals(Either.right("1").hashCode(), Either.right("2").hashCode());
        assertNotEquals(Either.<Object, Object>right("1"), Either.right(1));

        assertEquals(Either.left("1"), Either.left("1"));
        assertEquals(Either.left("1").hashCode(), Either.left("1").hashCode());

        assertNotEquals(Either.left("1"), Either.left("2"));
        assertNotEquals(Either.left("1").hashCode(), Either.left("2").hashCode());
        assertNotEquals(Either.<Object, Object>left("1"), Either.left(1));

        assertNotEquals(Either.right("1"), Either.left("1"));
    }

    @Test
    void testToString() {
        assertEquals("Right[value=1]", Either.right("1").toString());
        assertEquals("Left[value=1]", Either.left("1").toString());
    }

    @Test
    void testMap() {
        Either<String, String> left = Either.left("1");
        assertSame(left, left.map(s -> "A")); // Left is unchanged
        Either<?, String> right = Either.right("1");
        assertEquals(Either.right(1), right.map(Integer::parseInt));
    }

    @Test
    void testMapLeft() {
        Either<String, ?> left = Either.left("1");
        assertEquals(Either.left(1), left.mapLeft(Integer::parseInt));
        Either<String, String> right = Either.right("1");
        assertSame(right, right.mapLeft(s -> "A")); // Right is unchanged
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
        IllegalStateException x = assertThrows(IllegalStateException.class, () -> left.getRightOrElseThrow(IllegalStateException::new));
        assertEquals("1", x.getMessage());
        Either<String, String> right = Either.right("2");
        assertEquals("2", right.getRightOrElseThrow(IllegalArgumentException::new));
    }

    @Test
    void testPin() {
        Either<String, Integer> left = Either.left("1");
        Either<String, LocalDate> left2 = left.pinLeft();
        assertThrows(UnsupportedOperationException.class, left::pinRight);
        assertEquals(Either.left("1"), left2);
        Either<Integer, String> right = Either.right("2");
        Either<LocalDate, String> right2 = right.pinRight();
        assertThrows(UnsupportedOperationException.class, right::pinLeft);
        assertEquals(Either.right("2"), right2);
    }
}
