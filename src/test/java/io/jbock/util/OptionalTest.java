package io.jbock.util;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionalTest {

    @Test
    void testEquals() {
        new EqualsTester()
                .addEqualityGroup(Optional.of("1"), Optional.of("1"))
                .addEqualityGroup(Optional.of("2"), Optional.of("2"))
                .testEquals();
        new EqualsTester()
                .addEqualityGroup(Optional.of("1"))
                .addEqualityGroup(LeftOptional.of("1"))
                .testEquals();
        new EqualsTester()
                .addEqualityGroup(Optional.of("1"))
                .addEqualityGroup(Optional.empty(), Optional.empty())
                .testEquals();
        new EqualsTester()
                .addEqualityGroup(LeftOptional.empty())
                .addEqualityGroup(Optional.empty())
                .testEquals();
    }

    @Test
    void testToString() {
        assertEquals("Optional[1]", Optional.of("1").toString());
        assertEquals("Optional.empty", Optional.empty().toString());
    }

    @Test
    void testOrElseLeft() {
        assertEquals(Either.right("1"), Optional.of("1").orElseLeft(() -> 2));
        assertEquals(Either.left(2), Optional.empty().orElseLeft(() -> 2));
    }

    @Test
    void testFlatMapLeft() {
        assertEquals(Either.right("1"), Optional.of("1").flatMapLeft(() -> Either.left("L")));
        assertEquals(Either.right("1"), Optional.of("1").flatMapLeft(() -> Either.right("R")));
        assertEquals(Either.left("L"), Optional.empty().flatMapLeft(() -> Either.left("L")));
        assertEquals(Either.right("R"), Optional.empty().flatMapLeft(() -> Either.right("R")));
    }

    @Test
    void testOfNullable() {
        assertEquals(Optional.empty(), Optional.ofNullable(null));
        assertEquals(Optional.of("1"), Optional.ofNullable("1"));
    }

    @Test
    void testFilter() {
        assertEquals(Optional.empty(), Optional.of("1").filter(s -> false));
        assertEquals(Optional.of("1"), Optional.of("1").filter(s -> true));
    }

    @Test
    void testMap() {
        assertEquals(Optional.of(1), Optional.of("1").map(s -> 1));
        assertEquals(Optional.empty(), Optional.empty().map(s -> 1));
    }
}