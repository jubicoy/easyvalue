package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NullableTest {
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

    @Test
    void optionalPropertiesCanBeLeftBlank() {
        assertDoesNotThrow(
                () -> OptionalTestObject.builder().build()
        );
    }

    @Test
    void unpopulatedOptionalPropertyIsInitializedAsEmpty() {
        assertFalse(
                OptionalTestObject.builder()
                        .build()
                        .getProperty()
                        .isPresent()
        );
    }

    @Test
    void optionalPropertyIsInitialized() {
        assertEquals(
                "prop",
                OptionalTestObject.builder()
                        .setProperty("prop")
                        .build()
                        .getProperty()
                        .get()
        );
    }

    @EasyValue
    abstract static class OptionalTestObject {
        abstract Optional<String> getProperty();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_NullableTest_OptionalTestObject.Builder {

        }
    }
}
