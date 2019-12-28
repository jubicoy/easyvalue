package fi.jubic.easyvalue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class AnnotationCopyTest {
    @Test
    void copyAnnotationsTest() {
        assertNotNull(
                EasyValue_AnnotationCopyTest_TestClass.class.getAnnotation(CustomAnnotation.class)
        );

        JsonDeserialize jsonDeserialize = EasyValue_AnnotationCopyTest_TestClass.class
                .getAnnotation(JsonDeserialize.class);
        assertNotNull(jsonDeserialize);
        assertEquals(Void.class, jsonDeserialize.as());
        assertNotEquals(Void.class, jsonDeserialize.builder());

        assertNull(
                EasyValue_AnnotationCopyTest_TestClass.class.getAnnotation(JsonSerialize.class)
        );
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface CustomAnnotation {
    }

    @EasyValue
    @CustomAnnotation
    @JsonDeserialize(as = EasyValue_AnnotationCopyTest_TestClass.class)
    @JsonSerialize(as = EasyValue_AnnotationCopyTest_TestClass.class)
    abstract static class TestClass {
        @EasyProperty
        abstract Long id();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_AnnotationCopyTest_TestClass.Builder {
        }
    }
}
