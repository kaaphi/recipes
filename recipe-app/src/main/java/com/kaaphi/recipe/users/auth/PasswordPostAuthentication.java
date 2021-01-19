package com.kaaphi.recipe.users.auth;

import io.javalin.http.Context;

public class PasswordPostAuthentication extends PasswordAuthentication {

  @Override
  public String getUsername(Context ctx) {
    return ctx.formParam("username");
  }

  @Override
  protected String getPassword(Context ctx) {
    return ctx.formParam("password");
  }

}
