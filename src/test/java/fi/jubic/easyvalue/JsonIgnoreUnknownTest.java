package fi.jubic.easyvalue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonIgnoreUnknownTest {
    @Test
    void ignoreUnknownCopiedTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestClassA instanceA = mapper.readValue(
                "{\"id\": 12, \"unknown\": \"asd\"}",
                TestClassA.class
        );
        assertEquals(Long.valueOf(12L), instanceA.id());

        TestClassB instanceB = mapper.readValue(
                "{\"id\": 12, \"unknown\": \"asd\"}",
                TestClassB.class
        );
        assertEquals(Long.valueOf(12L), instanceB.id());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_JsonIgnoreUnknownTest_TestClassA.class)
    @JsonSerialize(as = EasyValue_JsonIgnoreUnknownTest_TestClassA.class)
    abstract static class TestClassA {
        @EasyProperty
        abstract Long id();

        static Builder builder() {
            return new Builder();
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Builder extends EasyValue_JsonIgnoreUnknownTest_TestClassA.Builder {
        }
    }

    @EasyValue
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonDeserialize(as = EasyValue_JsonIgnoreUnknownTest_TestClassB.class)
    @JsonSerialize(as = EasyValue_JsonIgnoreUnknownTest_TestClassB.class)
    abstract static class TestClassB {
        @EasyProperty
        abstract Long id();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_JsonIgnoreUnknownTest_TestClassB.Builder {
        }
    }
}
