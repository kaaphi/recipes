package com.kaaphi.recipe.app;

import com.kaaphi.recipe.users.User;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import java.util.Optional;

public class SessionAttributes {

  private SessionAttributes() {}

  public static final String CURRENT_USER = "currentUser";
  public static final String CURRENT_ROLES = "currentRoles";

  public static User getUser(Context ctx) {
    return Optional.ofNullable(ctx.<User>sessionAttribute(CURRENT_USER))
        .orElseThrow(UnauthorizedResponse::new);
  }
}
