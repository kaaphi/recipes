package com.kaaphi.recipe.users;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class AuthenticationMethods {
  private static ServiceLoader<AuthenticationMethod> authMethodLoader = ServiceLoader.load(AuthenticationMethod.class);

  public static AuthenticationMethod getAuthenticationMethod(String name) {
    return StreamSupport.stream(authMethodLoader.spliterator(), false)
    .filter(method -> name.equals(method.getName()))
    .findFirst()
    .orElseThrow(() -> new IllegalArgumentException("No authentication method registered for type"  + name));
  }  
}
