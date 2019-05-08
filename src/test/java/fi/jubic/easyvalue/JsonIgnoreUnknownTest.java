package fi.jubic.easyvalue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class JsonIgnoreUnknownTest {
    @Test
    public void ignoreUnknownCopiedTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestClassA instanceA = mapper.readValue(
                "{\"id\": 12, \"unknown\": \"asd\"}",
                TestClassA.class
        );
        Assert.assertEquals(Long.valueOf(12L), instanceA.id());

        TestClassB instanceB = mapper.readValue(
                "{\"id\": 12, \"unknown\": \"asd\"}",
                TestClassB.class
        );
        Assert.assertEquals(Long.valueOf(12L), instanceB.id());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_JsonIgnoreUnknownTest_TestClassA.class)
    @JsonSerialize(as = EasyValue_JsonIgnoreUnknownTest_TestClassA.class)
    static abstract class TestClassA {
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
    static abstract class TestClassB {
        @EasyProperty
        abstract Long id();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_JsonIgnoreUnknownTest_TestClassB.Builder {
        }
    }
}
