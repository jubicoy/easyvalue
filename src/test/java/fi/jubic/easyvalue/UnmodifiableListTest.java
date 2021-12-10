package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnmodifiableListTest {
    @Test
    void simpleBuildTest() {
        List<String> strings = Arrays.asList("a", "b", "c");
        ListValue value = new ListValue.Builder()
                .setStrings(strings)
                .build();

        assertEquals(
                strings,
                value.getStrings()
        );
        assertNotSame(strings, value.getStrings());
    }

    @Test
    void nullableValueTest() {
        NullableListValue value = new NullableListValue.Builder()
                .setStrings(null)
                .build();

        assertNull(value.getStrings());
    }

    @Test
    void accessorsReturnUnmodifiableTest() {
        assertThrows(
                IllegalStateException.class,
                () -> {
                    new ListValue.Builder()
                            .setStrings(new ArrayList<>())
                            .build()
                            .getStrings()
                            .add("b");
                }
        );

        assertThrows(
                IllegalStateException.class,
                () -> {
                    new ListValue.Builder()
                            .setStrings(new ArrayList<>())
                            .getStrings()
                            .add("b");
                }
        );
    }

    @EasyValue
    abstract static class ListValue {
        abstract List<String> getStrings();

        static class Builder extends EasyValue_UnmodifiableListTest_ListValue.Builder {

        }
    }

    @EasyValue
    abstract static class NullableListValue {
        @Nullable abstract List<String> getStrings();

        static class Builder extends EasyValue_UnmodifiableListTest_NullableListValue.Builder {

        }
    }
}
