package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericsTest {
    @Test
    void genericBuildable() {
        TestGeneric<String> object = TestGeneric.<String>builder()
                .setValue("This is value")
                .setAnotherValue("This is another value")
                .build();

        assertEquals("This is value", object.getValue());
        assertEquals("This is another value", object.getAnotherValue());

        TestGeneric<String> modified = object.toBuilder()
                .setValue("Yet another value")
                .build();

        assertEquals("Yet another value", modified.getValue());
        assertEquals("This is another value", modified.getAnotherValue());
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

        assertEquals("This is value", parent.getValue().getValue());
        assertEquals("This is another value", parent.getValue().getAnotherValue());
        assertEquals("Another", parent.getAnotherValue());
    }

    @EasyValue
    abstract static class TestGeneric<T> {
        abstract T getValue();

        abstract String getAnotherValue();

        abstract Builder<T> toBuilder();

        static <T> Builder<T> builder() {
            return new Builder<>();
        }

        static class Builder<T> extends EasyValue_GenericsTest_TestGeneric.Builder<T> {
        }
    }
}
