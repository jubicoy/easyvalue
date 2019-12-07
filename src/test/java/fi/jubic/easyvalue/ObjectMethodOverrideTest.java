package fi.jubic.easyvalue;

import org.junit.Assert;
import org.junit.Test;

public class ObjectMethodOverrideTest {
    @Test
    public void overrideToStringTest() {
        TestObject object = TestObject.of(23L);
        Assert.assertEquals("toString", object.toString());

        ControlObject control = ControlObject.of(23L);
        Assert.assertEquals(
                "fi.jubic.easyvalue.ObjectMethodOverrideTest.ControlObject{value=23}",
                control.toString()
        );
    }

    @Test
    public void overrideHashCodeTest() {
        TestObject object = TestObject.of(23L);
        Assert.assertEquals(130898, object.hashCode());

        ControlObject control = ControlObject.of(23L);
        Assert.assertEquals(1000020, control.hashCode());
    }

    @Test
    public void overrideEqualsTest() {
        TestObject object1 = TestObject.of(23L);
        TestObject object2 = TestObject.of(23L);
        TestObject object3 = TestObject.of(12L);

        Assert.assertNotEquals(object1, object1);
        Assert.assertNotEquals(object1, object2);
        Assert.assertNotEquals(object1, object3);
        Assert.assertNotEquals(object2, object3);

        ControlObject control1 = ControlObject.of(23L);
        ControlObject control2 = ControlObject.of(23L);
        ControlObject control3 = ControlObject.of(12L);

        Assert.assertEquals(control1, control1);
        Assert.assertEquals(control1, control2);
        Assert.assertNotEquals(control1, control3);
        Assert.assertNotEquals(control2, control3);
    }

    @EasyValue(excludeJson = true)
    abstract static class TestObject {
        @EasyProperty
        abstract Long value();

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

    @EasyValue(excludeJson = true)
    abstract static class ControlObject {
        @EasyProperty
        abstract Long value();

        static ControlObject of(Long value) {
            return new EasyValue_ObjectMethodOverrideTest_ControlObject(value);
        }
    }
}
