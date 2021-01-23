package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.app.SessionAttributes;
import com.kaaphi.recipe.users.User;
import io.javalin.http.Context;
import java.util.Optional;

public class PasswordPostAuthentication extends PasswordAuthentication {

  @Override
  public String getUsername(Context ctx) {
    User currentUser = ctx.sessionAttribute(SessionAttributes.CURRENT_USER);

    return ctx.formParam("username", Optional.ofNullable(currentUser).map(User::getUsername).orElse(null));
  }

  @Override
  protected String getPassword(Context ctx) {
    return ctx.formParam("password");
  }

  @Override
  protected String getNewPassword(Context ctx) { return ctx.formParam("newPassword"); }
}
