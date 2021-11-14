package com.yesfoss.imworker.utils;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtils {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private JacksonUtils() {}

  public static Map<String, String> convertJsonStringToMap(String jsonString)
      throws JsonProcessingException {
    return objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {});
  }

}
