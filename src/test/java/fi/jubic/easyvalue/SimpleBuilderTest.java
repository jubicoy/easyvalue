package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleBuilderTest {
    @Test
    void builderCreatesSpecifiedObjects() {
        TestUser user = TestUser.builder()
                .setId(5L)
                .setName("Richard")
                .build();
        assertEquals(5L, user.getId());
        assertEquals("Richard", user.getName());
    }

    @Test
    void toBuilderCycleGeneratesIdentical() {
        TestUser user = TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build()
                .toBuilder()
                .build();
        assertEquals(10L, user.getId());
        assertEquals("RMS", user.getName());
    }

    @Test
    void toBuilderAllowsChanges() {
        TestUser user = TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build()
                .toBuilder()
                .setId(12L)
                .build();
        assertEquals(12L, user.getId());
        assertEquals("RMS", user.getName());
    }

    @EasyValue
    abstract static class TestUser {
        abstract long getId();

        abstract String getName();

        abstract Builder toBuilder();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_SimpleBuilderTest_TestUser.Builder {
        }
    }
}
