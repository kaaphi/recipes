package com.kaaphi.recipe.users.auth;

import io.javalin.http.Context;

public class BasicAuthentication extends PasswordAuthentication {

  @Override
  protected String getPassword(Context ctx) {
    return ctx.basicAuthCredentials().getPassword();
  }

  @Override
  protected String getUsername(Context ctx) {
    return ctx.basicAuthCredentials().getUsername();
  }

}
