package io.jbock.util;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EitherTest {

    @Test
    void testEquals() {
        new EqualsTester()
                .addEqualityGroup(Either.right("1"), Either.right("1"))
                .addEqualityGroup(Either.right("2"), Either.right("2"))
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
        assertEquals(LeftOptional.of("1"), either.getLeft());
        assertEquals(Optional.empty(), either.getRight());
    }

    @Test
    void testGetRight() {
        Either<?, String> either = Either.right("1");
        assertTrue(either.isRight());
        assertFalse(either.isLeft());
        assertEquals(Optional.of("1"), either.getRight());
        assertEquals(LeftOptional.empty(), either.getLeft());
    }
}