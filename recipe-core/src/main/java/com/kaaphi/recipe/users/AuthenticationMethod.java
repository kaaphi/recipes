package com.kaaphi.recipe.users;

import io.javalin.Context;

public interface AuthenticationMethod {
  User authenticate(Context ctx, UserRepository repo);
  String generateNewUserAuthenticationDetails(Context ctx);
  String getDetailsType();
  default String getName() {
    return getClass().getName();
  }
}
