package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ObjectInterfaceTest {
    @Test
    void handlesValueEquality() {
        assertEquals(
                TestUser.of(10L, "RMS"),
                TestUser.of(10L, "RMS")
        );

        assertNotEquals(
                TestUser.of(11L, "RMS"),
                TestUser.of(10L, "RMS")
        );
        assertNotEquals(
                TestUser.of(10L, "Richard"),
                TestUser.of(10L, "RMS")
        );
    }

    @EasyValue
    abstract static class TestUser {
        abstract Long getId();

        abstract String getName();

        static TestUser of(Long id, String name) {
            return new EasyValue_ObjectInterfaceTest_TestUser(id, name);
        }
    }
}
