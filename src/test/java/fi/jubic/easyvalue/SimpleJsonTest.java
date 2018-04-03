package fi.jubic.easyvalue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SimpleJsonTest {


    @Test
    public void simpleSerialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Assert.assertEquals(
                "{\"name\":\"Richard\",\"id\":5}",
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
                "{\"name\":\"Stallman\",\"id\":12}",
                TestUser.class
        );
        Assert.assertEquals(new Long(12L), user.id());
        Assert.assertEquals("Stallman", user.name());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_SimpleJsonTest_TestUser.class)
    @JsonSerialize(as = EasyValue_SimpleJsonTest_TestUser.class)
    static abstract class TestUser {
        @EasyProperty
        public abstract Long id();

        @EasyProperty
        public abstract String name();

        public abstract Builder toBuilder();

        public static Builder builder() {
            return EasyValue_SimpleJsonTest_TestUser.getBuilder();
        }

        static class Builder extends EasyValue_SimpleJsonTest_TestUser.BuilderWrapper {
            @Override
            public EasyValue_SimpleJsonTest_TestUser.BuilderWrapper create() {
                return new Builder();
            }
        }
    }
}
