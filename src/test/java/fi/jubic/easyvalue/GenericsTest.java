package fi.jubic.easyvalue;

import org.junit.Assert;
import org.junit.Test;

public class GenericsTest {
    @Test
    public void genericBuildable() {
        TestGeneric<String> object = TestGeneric.<String>builder()
                .setValue("This is value")
                .setAnotherValue("This is another value")
                .build();

        Assert.assertEquals("This is value", object.value());
        Assert.assertEquals("This is another value", object.anotherValue());

        TestGeneric<String> modified = object.toBuilder()
                .setValue("Yet another value")
                .build();

        Assert.assertEquals("Yet another value", modified.value());
        Assert.assertEquals("This is another value", modified.anotherValue());
    }

    @Test
    public void nestedGenericBuildable() {
        TestGeneric<String> object = TestGeneric.<String>builder()
                .setValue("This is value")
                .setAnotherValue("This is another value")
                .build();

        TestGeneric<TestGeneric<String>> parent = TestGeneric.<TestGeneric<String>>builder()
                .setValue(object)
                .setAnotherValue("Another")
                .build();

        Assert.assertEquals("This is value", parent.value().value());
        Assert.assertEquals("This is another value", parent.value().anotherValue());
        Assert.assertEquals("Another", parent.anotherValue());
    }

    @EasyValue
    abstract static class TestGeneric<T> {
        @EasyProperty
        abstract T value();

        @EasyProperty
        abstract String anotherValue();

        abstract Builder<T> toBuilder();

        static <T> Builder<T> builder() {
            return new Builder<>();
        }

        static class Builder<T> extends EasyValue_GenericsTest_TestGeneric.Builder<T> {
        }
    }
}
