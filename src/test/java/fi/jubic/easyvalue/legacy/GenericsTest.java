package fi.jubic.easyvalue.legacy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericsTest {
    @Test
    void genericBuildable() {
        TestGeneric<String> object = TestGeneric.<String>builder()
                .setValue("This is value")
                .setAnotherValue("This is another value")
                .build();

        assertEquals("This is value", object.value());
        assertEquals("This is another value", object.anotherValue());

        TestGeneric<String> modified = object.toBuilder()
                .setValue("Yet another value")
                .build();

        assertEquals("Yet another value", modified.value());
        assertEquals("This is another value", modified.anotherValue());
    }

    @Test
    void nestedGenericBuildable() {
        TestGeneric<String> object = TestGeneric.<String>builder()
                .setValue("This is value")
                .setAnotherValue("This is another value")
                .build();

        TestGeneric<TestGeneric<String>> parent = TestGeneric.<TestGeneric<String>>builder()
                .setValue(object)
                .setAnotherValue("Another")
                .build();

        assertEquals("This is value", parent.value().value());
        assertEquals("This is another value", parent.value().anotherValue());
        assertEquals("Another", parent.anotherValue());
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
