package com.kaaphi.recipe.admin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;

public class AdminModule extends AbstractModule {

  @Provides
  ObjectMapper provideObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }


  @Provides @Singleton
  AdminConfig provideAdminConfig() {
    return new AdminConfig();
  }

  @Provides @Singleton
  HttpClientContext provideHttpClientContext(AdminConfig config) {
    HttpClientContext localContext = HttpClientContext.create();
    localContext.setCookieStore(new BasicCookieStore());
    return localContext;
  }

  @Provides
  CloseableHttpClient provideHttpClient() {
    return HttpClients.createDefault();
  }
}
