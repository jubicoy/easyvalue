package fi.jubic.easyvalue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleBuilderTest {
    @Test
    void builderCreatesSpecifiedObjects() {
        TestUser user = TestUser.builder()
                .setId(5L)
                .setName("Richard")
                .build();
        assertEquals(5L, user.id());
        assertEquals("Richard", user.name());
    }

    @Test
    void toBuilderCycleGeneratesIdentical() {
        TestUser user = TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build()
                .toBuilder()
                .build();
        assertEquals(10L, user.id());
        assertEquals("RMS", user.name());
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
        assertEquals(12L, user.id());
        assertEquals("RMS", user.name());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_SimpleBuilderTest_TestUser.class)
    @JsonSerialize(as = EasyValue_SimpleBuilderTest_TestUser.class)
    abstract static class TestUser {
        @EasyProperty
        abstract long id();

        @EasyProperty
        abstract String name();

        abstract Builder toBuilder();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_SimpleBuilderTest_TestUser.Builder {
        }
    }
}
