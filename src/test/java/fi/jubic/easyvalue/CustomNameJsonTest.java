package fi.jubic.easyvalue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CustomNameJsonTest {
    @Test
    public void customNameSerializeTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Assert.assertEquals(
                "{\"n_a_m_e\":\"Richard\",\"id\":5}",
                mapper.writeValueAsString(
                        TestUser.builder()
                                .setId(5L)
                                .setName("Richard")
                                .build()
                )
        );
    }

    @Test
    public void customNameDeserializeTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TestUser user = mapper.readValue(
                "{\"n_a_m_e\":\"Stallman\",\"id\":12}",
                TestUser.class
        );
        Assert.assertEquals(new Long(12L), user.id());
        Assert.assertEquals("Stallman", user.name());
    }

    @EasyValue
    @JsonDeserialize(as = EasyValue_CustomNameJsonTest_TestUser.class)
    @JsonSerialize(as = EasyValue_CustomNameJsonTest_TestUser.class)
    static abstract class TestUser {
        @EasyProperty
        abstract Long id();
        @EasyProperty("n_a_m_e")
        abstract String name();

        abstract Builder toBuilder();

        static Builder builder() {
            return EasyValue_CustomNameJsonTest_TestUser.getBuilder();
        }

        static class Builder extends EasyValue_CustomNameJsonTest_TestUser.BuilderWrapper {}
    }
}
