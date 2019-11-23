package com.kaaphi.recipe.users;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.kaaphi.recipe.users.auth.AuthenticationMethod;

public class AuthenticatableUser {
  private final User user;
  private final Map<String,String> authDetails;
  
  public AuthenticatableUser(String username, String authDetailsType, String authDetailValue) {
    this(new User(username), Collections.singletonMap(authDetailsType, authDetailValue));
  }
  
  public AuthenticatableUser(User user, Map<String,String> authDetails) {
    this.user = user;
    this.authDetails = Collections.unmodifiableMap(new HashMap<>(authDetails));
  }
  
  public User getUser() {
    return user;
  }
  
  public String getAuthDetails(AuthenticationMethod authMethod) {
    return getAuthDetails(authMethod.getDetailsType());
  }
  
  public String getAuthDetails(String authType) {
    return authDetails.get(authType);
  }
  
  public Map<String,String> getAuthDetails() {
    return authDetails;
  }
}
