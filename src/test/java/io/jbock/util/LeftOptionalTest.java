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
}