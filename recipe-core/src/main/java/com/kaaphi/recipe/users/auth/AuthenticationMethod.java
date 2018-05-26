package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import io.javalin.Context;

public interface AuthenticationMethod {
  User authenticate(Context ctx, UserRepository repo);
  String generateNewUserAuthenticationDetails(Context ctx);
  String getDetailsType();
  default String getName() {
    return getClass().getName();
  }
}
