package com.kaaphi.recipe.admin;

import java.net.URI;

public class AdminConfig {
  private String user;
  private char[] password;
  private URI baseUri;

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public char[] getPassword() {
    return password;
  }

  public void setPassword(char[] password) {
    this.password = password;
  }

  public URI getBaseUri() {
    return baseUri;
  }

  public void setBaseUri(URI baseUri) {
    this.baseUri = baseUri;
  }
}
