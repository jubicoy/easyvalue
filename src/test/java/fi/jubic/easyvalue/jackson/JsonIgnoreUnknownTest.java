package fi.jubic.easyvalue.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.jubic.easyvalue.EasyValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonIgnoreUnknownTest {
    @Test
    void ignoreUnknownForBuilder() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestClassA instanceA = mapper.readValue(
                "{\"id\": 12, \"unknown\": \"asd\"}",
                TestClassA.class
        );
        assertEquals(Long.valueOf(12L), instanceA.getId());
    }

    @EasyValue
    @JsonDeserialize(builder = TestClassA.Builder.class)
    abstract static class TestClassA {
        abstract Long getId();

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Builder extends EasyValue_JsonIgnoreUnknownTest_TestClassA.Builder {
        }
    }
}
