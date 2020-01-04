package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class AnnotationCopyTest {
    @Test
    void copyAnnotationsTest() {
        assertNotNull(
                EasyValue_AnnotationCopyTest_TestClass.class.getAnnotation(CustomAnnotation.class)
        );
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface CustomAnnotation {
    }

    @EasyValue
    @CustomAnnotation
    abstract static class TestClass {
        abstract Long getId();

        static class Builder extends EasyValue_AnnotationCopyTest_TestClass.Builder {
        }
    }
}
