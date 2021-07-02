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
}