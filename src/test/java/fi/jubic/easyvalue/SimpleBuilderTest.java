package fi.jubic.easyvalue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Assert;
import org.junit.Test;

public class SimpleBuilderTest {
    @Test
    public void builderCreatesSpecifiedObjects() {
        TestUser user = TestUser.builder()
                .setId(5L)
                .setName("Richard")
                .build();
        Assert.assertEquals(new Long(5L), user.id());
        Assert.assertEquals("Richard", user.name());
    }

    @Test
    public void toBuilderCycleGeneratesIdentical() {
        TestUser user = TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build()
                .toBuilder()
                .build();
        Assert.assertEquals(new Long(10L), user.id());
        Assert.assertEquals("RMS", user.name());
    }

    @Test
    public void toBuilderAllowsChanges() {
        TestUser user = TestUser.builder()
                .setId(10L)
                .setName("RMS")
                .build()
                .toBuilder()
                .setId(12L)
                .build();
        Assert.assertEquals(new Long(12L), user.id());
        Assert.assertEquals("RMS", user.name());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_SimpleBuilderTest_TestUser.class)
    @JsonSerialize(as = EasyValue_SimpleBuilderTest_TestUser.class)
    static abstract class TestUser {
        @EasyProperty
        abstract Long id();
        @EasyProperty
        abstract String name();

        abstract Builder toBuilder();

        static Builder builder() {
            return EasyValue_SimpleBuilderTest_TestUser.getBuilder();
        }

        static class Builder extends EasyValue_SimpleBuilderTest_TestUser.BuilderWrapper {
            @Override
            public EasyValue_SimpleBuilderTest_TestUser.BuilderWrapper create() {
                return new Builder();
            }
        }
    }
}
