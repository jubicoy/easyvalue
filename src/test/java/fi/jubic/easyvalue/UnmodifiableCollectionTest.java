package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnmodifiableCollectionTest {
    @Test
    void listBuildTest() {
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
    void nullableListTest() {
        assertNull(new NullableListValue.Builder().build().getStrings());
    }

    @Test
    void listAccessorsReturnUnmodifiableTest() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    new ListValue.Builder()
                            .setStrings(new ArrayList<>())
                            .build()
                            .getStrings()
                            .add("b");
                }
        );

        assertThrows(
                UnsupportedOperationException.class,
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

        static class Builder extends EasyValue_UnmodifiableCollectionTest_ListValue.Builder {

        }
    }

    @EasyValue
    abstract static class NullableListValue {
        @Nullable abstract List<String> getStrings();

        static class Builder
                extends EasyValue_UnmodifiableCollectionTest_NullableListValue.Builder {

        }
    }

    @Test
    void setBuildTest() {
        Set<String> strings = new HashSet<>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        SetValue value = new SetValue.Builder()
                .setStrings(strings)
                .build();

        assertEquals(
                strings,
                value.getStrings()
        );
        assertNotSame(strings, value.getStrings());
    }

    @Test
    void nullableSetTest() {
        assertNull(new NullableSetValue.Builder().build().getStrings());
    }

    @Test
    void setAccessorsReturnUnmodifiableTest() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    new SetValue.Builder()
                            .setStrings(new HashSet<>())
                            .build()
                            .getStrings()
                            .add("b");
                }
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    new SetValue.Builder()
                            .setStrings(new HashSet<>())
                            .getStrings()
                            .add("b");
                }
        );
    }

    @EasyValue
    abstract static class SetValue {
        abstract Set<String> getStrings();

        static class Builder extends EasyValue_UnmodifiableCollectionTest_SetValue.Builder {

        }
    }

    @EasyValue
    abstract static class NullableSetValue {
        @Nullable abstract Set<String> getStrings();

        static class Builder extends EasyValue_UnmodifiableCollectionTest_NullableSetValue.Builder {

        }
    }

    @Test
    void mapBuildTest() {
        Map<String, String> strings = new HashMap<>();
        strings.put("a", "a");
        strings.put("b", "b");
        strings.put("c", "c");
        MapValue value = new MapValue.Builder()
                .setStrings(strings)
                .build();

        assertEquals(
                strings,
                value.getStrings()
        );
        assertNotSame(strings, value.getStrings());
    }

    @Test
    void nullableMapTest() {
        assertNull(new NullableMapValue.Builder().build().getStrings());
    }

    @Test
    void mapAccessorsReturnUnmodifiableTest() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    new MapValue.Builder()
                            .setStrings(new HashMap<>())
                            .build()
                            .getStrings()
                            .put("b", "B");
                }
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> {
                    new MapValue.Builder()
                            .setStrings(new HashMap<>())
                            .getStrings()
                            .put("b", "B");
                }
        );
    }

    @EasyValue
    abstract static class MapValue {
        abstract Map<String, String> getStrings();

        static class Builder extends EasyValue_UnmodifiableCollectionTest_MapValue.Builder {

        }
    }

    @EasyValue
    abstract static class NullableMapValue {
        @Nullable abstract Map<String, String> getStrings();

        static class Builder extends EasyValue_UnmodifiableCollectionTest_NullableMapValue.Builder {

        }
    }
}
