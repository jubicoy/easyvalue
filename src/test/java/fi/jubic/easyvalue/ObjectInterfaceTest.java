package fi.jubic.easyvalue;

import org.junit.Assert;
import org.junit.Test;

public class ObjectInterfaceTest {
    @Test
    public void handlesValueEquality() {
        Assert.assertEquals(TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build(), TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build());

        Assert.assertNotEquals(TestUser.builder()
                .setId(11L)
                .setName("RMS")
                .build(), TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build());
        Assert.assertNotEquals(TestUser.builder()
                .setId(10L)
                .setName("Richard")
                .build(), TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build());
    }

    @EasyValue
    static abstract class TestUser {
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
