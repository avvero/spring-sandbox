package pw.avvero.spring.sandbox.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class TestUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static Object json(String value) throws JsonProcessingException {
        if (value == null || value.trim().length() == 0) return null;
        return OBJECT_MAPPER.readValue(value, Map.class);
    }

}
