package fi.jubic.easyvalue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomNameJsonTest {
    @Test
    void customNameSerializeTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(
                "{\"id\":5,\"n_a_m_e\":\"Richard\"}",
                mapper.writeValueAsString(
                        TestUser.builder()
                                .setId(5L)
                                .setName("Richard")
                                .build()
                )
        );
    }

    @Test
    void customNameDeserializeTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestUser user = mapper.readValue(
                "{\"id\":12,\"n_a_m_e\":\"Stallman\"}",
                TestUser.class
        );
        assertEquals(Long.valueOf(12L), user.id());
        assertEquals("Stallman", user.name());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_CustomNameJsonTest_TestUser.class)
    @JsonSerialize(as = EasyValue_CustomNameJsonTest_TestUser.class)
    abstract static class TestUser {
        @EasyProperty
        abstract Long id();

        @EasyProperty("n_a_m_e")
        abstract String name();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_CustomNameJsonTest_TestUser.Builder {
        }
    }
}
