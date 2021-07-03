package io.jbock.util;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(Optional.empty(), Optional.empty().filter(s -> false));
        assertEquals(Optional.empty(), Optional.empty().filter(s -> true));
    }

    @Test
    void testMap() {
        assertEquals(Optional.of(1), Optional.of("1").map(s -> 1));
        assertEquals(Optional.empty(), Optional.empty().map(s -> 1));
    }

    @Test
    void testFlatMap() {
        assertEquals(Optional.of(2), Optional.of("1").flatMap(s -> Optional.of(2)));
        assertEquals(Optional.empty(), Optional.of("1").flatMap(s -> Optional.empty()));
        assertEquals(Optional.empty(), Optional.empty().flatMap(s -> Optional.of(2)));
        assertEquals(Optional.empty(), Optional.empty().flatMap(s -> Optional.empty()));
    }

    @Test
    void testOr() {
        assertEquals(Optional.of("1"), Optional.of("1").or(() -> Optional.of("2")));
        assertEquals(Optional.of("1"), Optional.of("1").or(Optional::empty));
        assertEquals(Optional.of("2"), Optional.empty().or(() -> Optional.of("2")));
        assertEquals(Optional.empty(), Optional.empty().or(Optional::empty));
    }

    /*
     * The remaining tests are testing methods that are final in AbstractOptional,
     * so it's not necessary to have the corresponding test in LeftOptional.
     */

    @Test
    void testIsPresent() {
        assertTrue(Optional.of("1").isPresent());
        assertFalse(Optional.empty().isPresent());
    }

    @Test
    void testIsEmpty() {
        assertFalse(Optional.of("1").isEmpty());
        assertTrue(Optional.empty().isEmpty());
    }

    @Test
    void testIfPresent() {
        String[] output = {"1"};
        Optional.of("1").ifPresent(t -> output[0] = "Y");
        assertEquals("Y", output[0]);
        Optional.empty().ifPresent(t -> output[0] = "N");
        assertEquals("Y", output[0]);
    }

    @Test
    void testIfPresentOrElse() {
        String[] output1 = {"1"};
        String[] output2 = {"1"};
        Optional.of("1").ifPresentOrElse(t -> output1[0] = "Y", () -> output2[0] = "N");
        assertEquals("Y", output1[0]);
        assertEquals("1", output2[0]);
        Optional.empty().ifPresentOrElse(t -> output1[0] = "A", () -> output2[0] = "Z");
        assertEquals("Y", output1[0]);
        assertEquals("Z", output2[0]);
    }

    @Test
    void testStream() {
        assertEquals(List.of("1"), Optional.of("1").stream().collect(Collectors.toList()));
        assertEquals(List.of(), Optional.empty().stream().collect(Collectors.toList()));
    }

    @Test
    void testOrElse() {
        assertEquals("1", Optional.of("1").orElse("2"));
        assertEquals("2", Optional.empty().orElse("2"));
    }

    @Test
    void testOrElseGet() {
        assertEquals("1", Optional.of("1").orElseGet(() -> "2"));
        assertEquals("2", Optional.empty().orElseGet(() -> "2"));
    }

    @Test
    void testOrElseThrow() {
        Exception x = assertThrows(NoSuchElementException.class, () -> Optional.empty().orElseThrow());
        assertEquals("No value present", x.getMessage());
        assertEquals("2", Optional.of("2").orElseThrow());
    }

    @Test
    void testOrElseThrowWithSupplier() throws IOException {
        Exception x = assertThrows(IOException.class, () -> Optional.empty().orElseThrow(() -> new IOException("1")));
        assertEquals("1", x.getMessage());
        assertEquals("2", Optional.of("2").orElseThrow(() -> new IOException("1")));
    }
}
