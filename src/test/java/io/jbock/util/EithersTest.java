package io.jbock.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.jbock.util.Eithers.optionalList;
import static io.jbock.util.Eithers.toOptionalList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EithersTest {

    @Test
    void testOptionalList() {
        assertEquals(Optional.empty(), optionalList(List.of()));
        assertEquals(Optional.of(List.of(1)), optionalList(List.of(1)));
        assertEquals(Optional.of(List.of(1, 1)), optionalList(List.of(1, 1)));
    }

    @Test
    void testToOptionalList() {
        assertEquals(Optional.empty(), Stream.of().collect(toOptionalList()));
        assertEquals(Optional.of(List.of(1)), Stream.of(1).collect(toOptionalList()));
        assertEquals(Optional.of(List.of(1, 1)), Stream.of(1, 1).collect(toOptionalList()));
    }
}