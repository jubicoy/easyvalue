package fi.jubic.easyvalue;

import org.junit.Assert;
import org.junit.Test;

public class NoBuilderClassTest {
    @Test
    public void initializationTest() {
        TestObject object = TestObject.of(1L);
        Assert.assertEquals(1L, (long)object.value());
    }

    @EasyValue(excludeJson = true)
    abstract static class TestObject {
        @EasyProperty
        abstract Long value();

        static TestObject of(Long value) {
            return new EasyValue_NoBuilderClassTest_TestObject(value);
        }
    }
}
