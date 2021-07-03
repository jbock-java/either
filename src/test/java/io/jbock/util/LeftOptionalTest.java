package io.jbock.util;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeftOptionalTest {

    @Test
    void testEquals() {
        new EqualsTester()
                .addEqualityGroup(LeftOptional.of("1"), LeftOptional.of("1"))
                .addEqualityGroup(LeftOptional.of("2"), LeftOptional.of("2"))
                .testEquals();
        new EqualsTester()
                .addEqualityGroup(LeftOptional.empty(), LeftOptional.empty())
                .addEqualityGroup(LeftOptional.of("1"))
                .testEquals();
    }

    @Test
    void testToString() {
        assertEquals("LeftOptional[1]", LeftOptional.of("1").toString());
        assertEquals("LeftOptional.empty", LeftOptional.empty().toString());
    }

    @Test
    void testOrElseRight() {
        assertEquals(Either.left("1"), LeftOptional.of("1").orElseRight(() -> 2));
        assertEquals(Either.right(2), LeftOptional.empty().orElseRight(() -> 2));
    }

    @Test
    void testFlatMapRight() {
        assertEquals(Either.left("1"), LeftOptional.of("1").flatMapRight(() -> Either.left("L")));
        assertEquals(Either.left("1"), LeftOptional.of("1").flatMapRight(() -> Either.right("R")));
        assertEquals(Either.left("L"), LeftOptional.empty().flatMapRight(() -> Either.left("L")));
        assertEquals(Either.right("R"), LeftOptional.empty().flatMapRight(() -> Either.right("R")));
    }

    @Test
    void testOfNullable() {
        assertEquals(LeftOptional.empty(), LeftOptional.ofNullable(null));
        assertEquals(LeftOptional.of("1"), LeftOptional.ofNullable("1"));
    }

    @Test
    void testFilter() {
        assertEquals(LeftOptional.empty(), LeftOptional.of("1").filter(s -> false));
        assertEquals(LeftOptional.of("1"), LeftOptional.of("1").filter(s -> true));
        assertEquals(LeftOptional.empty(), LeftOptional.empty().filter(s -> false));
        assertEquals(LeftOptional.empty(), LeftOptional.empty().filter(s -> true));
    }

    @Test
    void testMap() {
        assertEquals(LeftOptional.of(1), LeftOptional.of("1").map(s -> 1));
        assertEquals(LeftOptional.empty(), LeftOptional.empty().map(s -> 1));
    }

    @Test
    void testFlatMap() {
        assertEquals(LeftOptional.of(2), LeftOptional.of("1").flatMap(s -> LeftOptional.of(2)));
        assertEquals(LeftOptional.empty(), LeftOptional.of("1").flatMap(s -> LeftOptional.empty()));
        assertEquals(LeftOptional.empty(), LeftOptional.empty().flatMap(s -> LeftOptional.of(2)));
        assertEquals(LeftOptional.empty(), LeftOptional.empty().flatMap(s -> LeftOptional.empty()));
    }

    @Test
    void testOr() {
        assertEquals(LeftOptional.of("1"), LeftOptional.of("1").or(() -> LeftOptional.of("2")));
        assertEquals(LeftOptional.of("1"), LeftOptional.of("1").or(LeftOptional::empty));
        assertEquals(LeftOptional.of("2"), LeftOptional.empty().or(() -> LeftOptional.of("2")));
        assertEquals(LeftOptional.empty(), LeftOptional.empty().or(LeftOptional::empty));
    }
}
