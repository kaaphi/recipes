package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import io.javalin.http.Context;

public interface AuthenticationMethod {
  User authenticate(Context ctx, UserRepository repo);
  default void updateAuthenticationDetails(Context ctx, UserRepository repo) {
    throw new UnsupportedOperationException();
  }
  String getDetailsType();
  default String getName() {
    return getClass().getName();
  }
}
