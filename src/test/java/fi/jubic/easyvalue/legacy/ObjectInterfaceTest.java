package fi.jubic.easyvalue.legacy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ObjectInterfaceTest {
    @Test
    void handlesValueEquality() {
        Assertions.assertEquals(
                TestUser.builder()
                        .setId(10L)
                        .setName("RMS")
                        .build(),
                TestUser.builder()
                        .setId(10L)
                        .setName("RMS")
                        .build()
        );

        Assertions.assertNotEquals(
                TestUser.builder()
                        .setId(11L)
                        .setName("RMS")
                        .build(),
                TestUser.builder()
                        .setId(10L)
                        .setName("RMS")
                        .build()
        );
        Assertions.assertNotEquals(
                TestUser.builder()
                        .setId(10L)
                        .setName("Richard")
                        .build(),
                TestUser.builder()
                        .setId(10L)
                        .setName("RMS")
                        .build()
        );
    }

    @EasyValue
    abstract static class TestUser {
        @EasyProperty
        abstract Long id();

        @EasyProperty
        abstract String name();

        abstract Builder toBuilder();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_ObjectInterfaceTest_TestUser.Builder {
        }
    }
}
