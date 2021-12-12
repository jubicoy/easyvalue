package fi.jubic.easyvalue;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultsTest {
    @Test
    void primitiveDefaultValueUsed() {
        // Not used
        PrimitiveClass obj1 = PrimitiveClass.builder()
                .setBooleanValue(true)
                .setIntegerValue(2)
                .setFloatValue(3.0f)
                .build();

        assertTrue(obj1.getBooleanValue());
        assertEquals(2, obj1.getIntegerValue());
        assertEquals(3.0f, obj1.getFloatValue());

        // Used
        PrimitiveClass obj2 = PrimitiveClass.builder()
                .setBooleanValue(true)
                .setIntegerValue(2)
                .build();

        assertTrue(obj2.getBooleanValue());
        assertEquals(2, obj2.getIntegerValue());
        assertEquals(1.0f, obj2.getFloatValue());
    }

    @EasyValue
    abstract static class PrimitiveClass {
        abstract boolean getBooleanValue();

        abstract int getIntegerValue();

        abstract float getFloatValue();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_DefaultsTest_PrimitiveClass.Builder {
            @Override
            public Builder defaults(Builder builder) {
                return builder.setFloatValue(1.0f);
            }
        }
    }

    @Test
    void objectDefaultValueUsed() {
        // Not used
        MixedClass obj1 = MixedClass.builder()
                .setIntegerValue(1)
                .setStringValue("not default")
                .build();

        assertEquals(1, obj1.getIntegerValue());
        assertEquals("not default", obj1.getStringValue());

        // Used
        MixedClass obj2 = MixedClass.builder()
                .setIntegerValue(1)
                .build();

        assertEquals(1, obj2.getIntegerValue());
        assertEquals("default", obj2.getStringValue());
    }

    @EasyValue
    abstract static class MixedClass {
        abstract int getIntegerValue();

        abstract String getStringValue();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_DefaultsTest_MixedClass.Builder {
            @Override
            public Builder defaults(Builder builder) {
                return builder.setStringValue("default");
            }
        }
    }

    @Test
    void collectionDefaultValueUsed() {
        // Not used
        CollectionsClass obj1 = CollectionsClass.builder()
                .setList(Collections.singletonList("a"))
                .setSet(Collections.singleton("a"))
                .setMap(Collections.singletonMap("a", "A"))
                .build();

        assertEquals(Collections.singletonList("a"), obj1.getList());
        assertEquals(Collections.singleton("a"), obj1.getSet());
        assertEquals(Collections.singletonMap("a", "A"), obj1.getMap());

        // Used
        CollectionsClass obj2 = CollectionsClass.builder().build();

        assertEquals(Collections.emptyList(), obj2.getList());
        assertEquals(Collections.emptySet(), obj2.getSet());
        assertEquals(Collections.emptyMap(), obj2.getMap());
    }

    @EasyValue
    abstract static class CollectionsClass {
        abstract List<String> getList();

        abstract Set<String> getSet();

        abstract Map<String, String> getMap();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_DefaultsTest_CollectionsClass.Builder {
            @Override
            public Builder defaults(Builder builder) {
                return builder.setList(Collections.emptyList())
                        .setSet(Collections.emptySet())
                        .setMap(Collections.emptyMap());
            }
        }
    }
}
