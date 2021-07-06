package io.jbock.util;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void testIsPresent() {
        assertTrue(LeftOptional.of("1").isPresent());
        assertFalse(LeftOptional.empty().isPresent());
    }

    @Test
    void testIsEmpty() {
        assertFalse(LeftOptional.of("1").isEmpty());
        assertTrue(LeftOptional.empty().isEmpty());
    }

    @Test
    void testIfPresent() {
        String[] output = {"1"};
        LeftOptional.of("1").ifPresent(t -> output[0] = "Y");
        assertEquals("Y", output[0]);
        LeftOptional.empty().ifPresent(t -> output[0] = "N");
        assertEquals("Y", output[0]);
    }

    @Test
    void testIfPresentOrElse() {
        String[] output1 = {"1"};
        String[] output2 = {"1"};
        LeftOptional.of("1").ifPresentOrElse(t -> output1[0] = "Y", () -> output2[0] = "N");
        assertEquals("Y", output1[0]);
        assertEquals("1", output2[0]);
        LeftOptional.empty().ifPresentOrElse(t -> output1[0] = "A", () -> output2[0] = "Z");
        assertEquals("Y", output1[0]);
        assertEquals("Z", output2[0]);
    }

    @Test
    void testStream() {
        assertEquals(List.of("1"), LeftOptional.of("1").stream().collect(Collectors.toList()));
        assertEquals(List.of(), LeftOptional.empty().stream().collect(Collectors.toList()));
    }

    @Test
    void testOrElse() {
        assertEquals("1", LeftOptional.of("1").orElse("2"));
        assertEquals("2", LeftOptional.empty().orElse("2"));
    }

    @Test
    void testOrElseGet() {
        assertEquals("1", LeftOptional.of("1").orElseGet(() -> "2"));
        assertEquals("2", LeftOptional.empty().orElseGet(() -> "2"));
    }

    @Test
    void testOrElseThrow() {
        Exception x = assertThrows(NoSuchElementException.class, () -> LeftOptional.empty().orElseThrow());
        assertEquals("No value present", x.getMessage());
        assertEquals("2", LeftOptional.of("2").orElseThrow());
    }

    @Test
    void testGet() {
        Exception x = assertThrows(NoSuchElementException.class, () -> LeftOptional.empty().get());
        assertEquals("No value present", x.getMessage());
        assertEquals("2", LeftOptional.of("2").get());
    }

    @Test
    void testOrElseThrowWithSupplier() throws IOException {
        Exception x = assertThrows(IOException.class, () -> LeftOptional.empty().orElseThrow(() -> new IOException("1")));
        assertEquals("1", x.getMessage());
        assertEquals("2", LeftOptional.of("2").orElseThrow(() -> new IOException("1")));
    }

    @Test
    void testEfficientEmpty() {
        LeftOptional<String> empty1 = LeftOptional.empty();
        LeftOptional<String> empty2 = LeftOptional.empty();
        assertSame(empty1, empty2);
    }
}
