package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NullableTest {
    @Test
    void nullablePropertiesCanBeLeftBlank() {
        assertDoesNotThrow(
                () -> TestObject.builder()
                        .setProperty2("value")
                        .build()
        );
    }

    @Test
    void notNullablePropertiesCannotBeLeftBlank() {
        assertThrows(
                IllegalStateException.class,
                () -> TestObject.builder()
                        .setProperty1("value")
                        .build()
        );
    }

    @EasyValue
    abstract static class TestObject {
        @Nullable
        abstract String getProperty1();

        abstract String getProperty2();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_NullableTest_TestObject.Builder {

        }
    }
}
