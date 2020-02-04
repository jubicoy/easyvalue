package fi.jubic.easyvalue.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.jubic.easyvalue.EasyValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultMissingPropertyTest {
    @Test
    void applyDefaultForMissingProperty() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TestClass instance = mapper.readValue(
                "{\"name\": \"Name\" }",
                TestClass.class
        );

        assertEquals(0L, instance.getId());
        assertEquals("Name", instance.getName());
    }

    @EasyValue
    @JsonDeserialize(builder = TestClass.Builder.class)
    abstract static class TestClass {
        public abstract Long getId();

        public abstract String getName();

        public static class Builder extends EasyValue_DefaultMissingPropertyTest_TestClass.Builder {
            @Override
            public Builder defaults(Builder builder) {
                return builder.setId(0L);
            }
        }
    }
}
