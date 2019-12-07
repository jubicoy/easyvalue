package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoBuilderClassTest {
    @Test
    void initializationTest() {
        TestObject object = TestObject.of(1L);
        assertEquals(1L, (long)object.value());
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
