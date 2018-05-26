package com.kaaphi.recipe.users;

import com.kaaphi.recipe.users.auth.AuthenticationMethod;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class User {
  private final String username;
  private final Map<String, String> authDetails;

  public User(String username, Map<String, String> authDetails) {
    this.username = username;
    this.authDetails = new HashMap<>(authDetails);
  }

  public String getUsername() {
    return username;
  }

  public Map<String, String> getAuthDetails() {
    return Collections.unmodifiableMap(authDetails);
  }
  
  public void setAuthDetails(AuthenticationMethod authMethod, String auth) {
    authDetails.put(authMethod.getName(), auth);
  }
  
  public String getAuthDetails(AuthenticationMethod authMethod) {
    return authDetails.get(authMethod.getDetailsType());
  }
}
