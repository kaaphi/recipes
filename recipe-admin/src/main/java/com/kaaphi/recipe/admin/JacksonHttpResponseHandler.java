package com.kaaphi.recipe.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class JacksonHttpResponseHandler<T> extends AbstractHttpClientResponseHandler<T> {
  private final ObjectMapper objectMapper;
  private final TypeReference<T> type;

  public JacksonHttpResponseHandler(ObjectMapper objectMapper, TypeReference<T> type) {
    this.objectMapper = objectMapper;
    this.type = type;
  }

  @Override
  public T handleEntity(HttpEntity entity) throws IOException {
    try(InputStream is = entity.getContent()) {
      if(type != null) {
        return objectMapper.readValue(is, type);
      } else {
        return null;
      }
    } finally {
      EntityUtils.consume(entity);
    }
  }
}
