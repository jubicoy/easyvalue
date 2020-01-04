package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ObjectMethodOverrideTest {
    @Test
    void overrideToStringTest() {
        TestObject object = TestObject.of(23L);
        assertEquals("toString", object.toString());

        ControlObject control = ControlObject.of(23L);
        assertEquals(
                "fi.jubic.easyvalue.ObjectMethodOverrideTest.ControlObject{value=23}",
                control.toString()
        );
    }

    @Test
    void overrideHashCodeTest() {
        TestObject object = TestObject.of(23L);
        assertEquals(130898, object.hashCode());

        ControlObject control = ControlObject.of(23L);
        assertEquals(Objects.hash(23L), control.hashCode());
    }

    @Test
    void overrideEqualsTest() {
        TestObject object1 = TestObject.of(23L);
        TestObject object2 = TestObject.of(23L);
        TestObject object3 = TestObject.of(12L);

        assertNotEquals(object1, object1);
        assertNotEquals(object1, object2);
        assertNotEquals(object1, object3);
        assertNotEquals(object2, object3);

        ControlObject control1 = ControlObject.of(23L);
        ControlObject control2 = ControlObject.of(23L);
        ControlObject control3 = ControlObject.of(12L);

        assertEquals(control1, control1);
        assertEquals(control1, control2);
        assertNotEquals(control1, control3);
        assertNotEquals(control2, control3);
    }

    @EasyValue
    abstract static class TestObject {
        abstract Long getValue();

        @Override
        public String toString() {
            return "toString";
        }

        @Override
        public int hashCode() {
            return 130898;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        static TestObject of(Long value) {
            return new EasyValue_ObjectMethodOverrideTest_TestObject(value);
        }
    }

    @EasyValue
    abstract static class ControlObject {
        abstract Long getValue();

        static ControlObject of(Long value) {
            return new EasyValue_ObjectMethodOverrideTest_ControlObject(value);
        }
    }
}
