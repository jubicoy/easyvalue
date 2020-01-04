package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoBuilderClassTest {
    @Test
    void initializationTest() {
        assertEquals(1L, TestObject.of(1L).getValue());
        assertEquals(10L, TestObject.of(10L).getValue());
    }

    @EasyValue
    abstract static class TestObject {
        abstract long getValue();

        static TestObject of(Long value) {
            return new EasyValue_NoBuilderClassTest_TestObject(value);
        }
    }
}
