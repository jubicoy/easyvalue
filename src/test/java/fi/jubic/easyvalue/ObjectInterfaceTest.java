package fi.jubic.easyvalue;

import org.junit.Assert;
import org.junit.Test;

public class ObjectInterfaceTest {
    @Test
    public void handlesValueEquality() {
        Assert.assertTrue(
                TestUser.builder()
                        .setId(10L)
                        .setName("RMS")
                        .build()
                        .equals(
                                TestUser.builder()
                                        .setId(10L)
                                        .setName("RMS")
                                        .build()
                        )

        );

        Assert.assertFalse(
                TestUser.builder()
                        .setId(11L)
                        .setName("RMS")
                        .build()
                        .equals(
                                TestUser.builder()
                                        .setId(10L)
                                        .setName("RMS")
                                        .build()
                        )

        );
        Assert.assertFalse(
                TestUser.builder()
                        .setId(10L)
                        .setName("Richard")
                        .build()
                        .equals(
                                TestUser.builder()
                                        .setId(10L)
                                        .setName("RMS")
                                        .build()
                        )

        );
    }

    @EasyValue
    static abstract class TestUser {
        @EasyProperty
        abstract Long id();
        @EasyProperty
        abstract String name();

        abstract Builder toBuilder();

        static Builder builder() {
            return EasyValue_ObjectInterfaceTest_TestUser.getBuilder();
        }

        static class Builder extends EasyValue_ObjectInterfaceTest_TestUser.BuilderWrapper {
            @Override
            public EasyValue_ObjectInterfaceTest_TestUser.BuilderWrapper create() {
                return new Builder();
            }
        }
    }
}
