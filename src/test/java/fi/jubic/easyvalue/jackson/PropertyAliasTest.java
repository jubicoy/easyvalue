package fi.jubic.easyvalue.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.jubic.easyvalue.EasyValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyAliasTest {
    @EasyValue
    @JsonDeserialize(builder = SymmetricObject.Builder.class)
    abstract static class SymmetricObject {
        public abstract Long getId();

        @JsonProperty("n_a_m_e")
        public abstract String getName();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_PropertyAliasTest_SymmetricObject.Builder {
        }
    }

    @Test
    void symmetricSerialize() {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.valueToTree(
                SymmetricObject.builder()
                        .setId(5L)
                        .setName("Richard")
                        .build()
        );
        assertEquals(5, json.get("id").asInt());
        assertEquals("Richard", json.get("n_a_m_e").asText());
    }

    @Test
    void symmetricDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        SymmetricObject obj = mapper.readValue(
                "{\"id\":12,\"n_a_m_e\":\"Stallman\"}",
                SymmetricObject.class
        );
        assertEquals(Long.valueOf(12L), obj.getId());
        assertEquals("Stallman", obj.getName());
    }

    @EasyValue
    @JsonDeserialize(builder = DeserializeAliasObject.Builder.class)
    abstract static class DeserializeAliasObject {
        public abstract Long getId();

        public abstract String getName();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_PropertyAliasTest_DeserializeAliasObject.Builder {
            @Override
            @JsonProperty("n_a_m_e")
            public Builder setName(String name) {
                return super.setName(name);
            }
        }
    }

    @Test
    void deserializeAliasSerialize() {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.valueToTree(
                DeserializeAliasObject.builder()
                        .setId(5L)
                        .setName("Richard")
                        .build()
        );
        assertEquals(5, json.get("id").asInt());
        assertEquals("Richard", json.get("name").asText());
    }

    @Test
    void deserializeAliasDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        DeserializeAliasObject obj = mapper.readValue(
                "{\"id\":12,\"n_a_m_e\":\"Stallman\"}",
                DeserializeAliasObject.class
        );
        assertEquals(Long.valueOf(12L), obj.getId());
        assertEquals("Stallman", obj.getName());
    }

    @EasyValue
    @JsonDeserialize(builder = SerializeAliasObject.Builder.class)
    abstract static class SerializeAliasObject {
        public abstract Long getId();

        @JsonProperty("n_a_m_e")
        public abstract String getName();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_PropertyAliasTest_SerializeAliasObject.Builder {
            @Override
            @JsonProperty
            public Builder setName(String name) {
                return super.setName(name);
            }
        }
    }

    @Test
    void serializeAliasSerialize() {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode json = mapper.valueToTree(
                SerializeAliasObject.builder()
                        .setId(5L)
                        .setName("Richard")
                        .build()
        );
        assertEquals(5, json.get("id").asInt());
        assertEquals("Richard", json.get("n_a_m_e").asText());
    }

    @Test
    void serializeAliasDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        DeserializeAliasObject obj = mapper.readValue(
                "{\"id\":12,\"name\":\"Stallman\"}",
                DeserializeAliasObject.class
        );
        assertEquals(Long.valueOf(12L), obj.getId());
        assertEquals("Stallman", obj.getName());
    }
}
