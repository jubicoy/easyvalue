package fi.jubic.easyvalue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class SimpleJsonTest {


    @Test
    public void simpleSerialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Assert.assertEquals(
                "{\"id\":5,\"name\":\"Richard\"}",
                mapper.writeValueAsString(
                        TestUser.builder()
                                .setId(5L)
                                .setName("Richard")
                                .build()
                )
        );
    }

    @Test
    public void simpleDeserialization() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestUser user = mapper.readValue(
                "{\"id\":12,\"name\":\"Stallman\"}",
                TestUser.class
        );
        Assert.assertEquals(Long.valueOf(12L), user.id());
        Assert.assertEquals("Stallman", user.name());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_SimpleJsonTest_TestUser.class)
    @JsonSerialize(as = EasyValue_SimpleJsonTest_TestUser.class)
    abstract static class TestUser {
        @EasyProperty
        abstract Long id();

        @EasyProperty
        abstract String name();

        public abstract Builder toBuilder();

        static Builder builder() {
            return new Builder();
        }

        static class Builder extends EasyValue_SimpleJsonTest_TestUser.Builder {
        }
    }
}
