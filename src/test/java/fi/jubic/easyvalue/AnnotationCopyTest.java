package fi.jubic.easyvalue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Assert;
import org.junit.Test;

public class AnnotationCopyTest {
    @Test
    public void copyAnnotationsTest() {
        Assert.assertNotNull(
                EasyValue_AnnotationCopyTest_TestClass.class.getAnnotation(CustomAnnotation.class)
        );

        JsonDeserialize jsonDeserialize = EasyValue_AnnotationCopyTest_TestClass.class
                .getAnnotation(JsonDeserialize.class);
        Assert.assertNotNull(jsonDeserialize);
        Assert.assertEquals(Void.class, jsonDeserialize.as());
        Assert.assertNotEquals(Void.class, jsonDeserialize.builder());

        Assert.assertNull(
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
