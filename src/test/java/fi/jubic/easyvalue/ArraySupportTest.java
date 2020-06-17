package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

class ArraySupportTest {
    @Test
    void testArrayEquals() {
        TestClass obj = TestClass.builder()
                .setId(1L)
                .setBytes(new byte[] { 1, 2, 3 })
                .build();

        assertEquals(obj, obj);
        assertEquals(
                obj,
                obj.toBuilder().build()
        );
        assertNotEquals(
                obj,
                obj.toBuilder()
                        .setBytes(new byte[] { 1, 2, 4 })
                        .build()
        );
        assertNotEquals(
                obj,
                obj.toBuilder()
                        .setId(2L)
                        .build()
        );
        assertNotEquals(
                obj,
                obj.toBuilder()
                        .setId(2L)
                        .setBytes(new byte[] { 1, 2, 4 })
                        .build()
        );
    }

    @Test
    void testToStringOmit() {
        TestClass obj = TestClass.builder()
                .setId(1L)
                .setBytes(new byte[] { 1, 2, 3 })
                .build();

        assertEquals(
                "fi.jubic.easyvalue.ArraySupportTest.TestClass{id=1}",
                obj.toString()
        );
    }

    @Test
    void testCopyOnArrayRead() {
        TestClass.Builder builder = TestClass.builder()
                .setId(1L)
                .setBytes(new byte[] { 1, 2, 3 });
        assertArrayEquals(builder.getBytes(), builder.getBytes());
        assertNotSame(builder.getBytes(), builder.getBytes());

        TestClass obj = builder.build();
        assertArrayEquals(obj.getBytes(), obj.getBytes());
        assertNotSame(obj.getBytes(), obj.getBytes());
    }

    @Test
    void testCopyOnNullableArrayRead() {
        NullableTestClass.Builder builder = NullableTestClass.builder()
                .setId(1L);
        assertNull(builder.getBytes());

        NullableTestClass obj = builder.build();
        assertNull(obj.getBytes());
    }

    @EasyValue
    abstract static class TestClass {
        public abstract Long getId();

        public abstract byte[] getBytes();

        public abstract Builder toBuilder();

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends EasyValue_ArraySupportTest_TestClass.Builder {

        }
    }

    @EasyValue
    abstract static class NullableTestClass {
        public abstract Long getId();

        @Nullable
        public abstract byte[] getBytes();

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends EasyValue_ArraySupportTest_NullableTestClass.Builder {

        }
    }
}
