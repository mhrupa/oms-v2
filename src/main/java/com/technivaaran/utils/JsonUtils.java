package com.technivaaran.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technivaaran.exceptions.OMSException;

public class JsonUtils {

    private JsonUtils() {
    }

    public static String toJson(Object data) throws OMSException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new OMSException("Error occurred while converting Object to JSON");
        }
    }
}
