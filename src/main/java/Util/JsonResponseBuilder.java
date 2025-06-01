package Util;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonResponseBuilder {
    private final Map<String, Object> response;

    public JsonResponseBuilder() {
        response = new LinkedHashMap<>();
    }

    public JsonResponseBuilder status(String status) {
        response.put("status", status);
        return this;
    }

    public JsonResponseBuilder message(String message) {
        response.put("message", message);
        return this;
    }

    public JsonResponseBuilder with(String key, Object value) {
        response.put(key, value);
        return this;
    }

    public String build() {
        return new Gson().toJson(response);
    }
}