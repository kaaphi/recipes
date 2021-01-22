package com.kaaphi.recipe.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import java.io.IOException;
import java.net.URI;
import java.util.function.Function;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class RecipeApiClient {
  private final AdminConfig config;
  private final Provider<CloseableHttpClient> clientProvider;
  private final HttpClientContext clientContext;
  private final ObjectMapper objectMapper;

  @Inject
  public RecipeApiClient(AdminConfig config,
      Provider<CloseableHttpClient> clientProvider,
      HttpClientContext clientContext, ObjectMapper objectMapper) {
    this.config = config;
    this.clientProvider = clientProvider;
    this.clientContext = clientContext;
    this.objectMapper = objectMapper;
  }

  public <T> T get(String path, TypeReference<T> type) throws IOException {
    return makeJacksonRequest(HttpGet::new, path, null, type);
  }

  public <T> T post(String path, Object requestBody, TypeReference<T> type) throws IOException {
    return makeJacksonRequest(HttpPost::new, path, requestBody, type);
  }

  public <T> T put(String path, Object requestBody, TypeReference<T> type) throws IOException {
    return makeJacksonRequest(HttpPut::new, path, requestBody, type);
  }

  public void delete(String path) throws IOException {
    makeJacksonRequest(HttpDelete::new, path, null, null);
  }

  private <T> T makeJacksonRequest(Function<URI, ? extends ClassicHttpRequest> requestBuilder, String path, Object requestBody, TypeReference<T> type)
      throws IOException {
    ClassicHttpRequest req = requestBuilder.apply(config.getBaseUri().resolve(path));
    if(requestBody != null) {
      if(requestBody instanceof HttpEntity) {
        req.setEntity((HttpEntity) requestBody);
      } else {
        req.setEntity(new StringEntity(objectMapper.writeValueAsString(requestBody),
            ContentType.APPLICATION_JSON, "UTF-8", false));
      }
    }
    return makeJacksonRequest(req, type);
  }

  private <T> T makeJacksonRequest(ClassicHttpRequest req, TypeReference<T> type) throws IOException {
    try(CloseableHttpClient client = clientProvider.get()) {
      return client.execute(req, clientContext, new JacksonHttpResponseHandler<>(objectMapper, type));
    }
  }

  private String makeStringRequest(ClassicHttpRequest req) throws IOException {
    try(CloseableHttpClient client = clientProvider.get()) {
      return client.execute(req, clientContext, new AbstractHttpClientResponseHandler<String>() {
        @Override
        public String handleEntity(HttpEntity entity) throws IOException {
          try {
            return EntityUtils.toString(entity);
          } catch (ParseException e) {
            throw new IOException(e);
          } finally {
            EntityUtils.consume(entity);
          }
        }
      });
    }
  }


}
