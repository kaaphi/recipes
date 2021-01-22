package com.kaaphi.recipe.admin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;

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
    BasicScheme basicAuth = new BasicScheme();
    basicAuth.initPreemptive(new UsernamePasswordCredentials(config.getUser(), config.getPassword()));
    HttpHost target = new HttpHost(config.getBaseUri().getScheme(), config.getBaseUri().getHost(), config.getBaseUri().getPort());

    HttpClientContext localContext = HttpClientContext.create();
    localContext.resetAuthExchange(target, basicAuth);
    return localContext;
  }

  @Provides
  CloseableHttpClient provideHttpClient() {
    return HttpClients.createDefault();
  }
}
