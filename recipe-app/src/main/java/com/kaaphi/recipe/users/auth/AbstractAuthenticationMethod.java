package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.app.SessionAttributes;
import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import java.util.Collections;

public abstract class AbstractAuthenticationMethod implements AuthenticationMethod {

  @Override
  public User authenticate(Context ctx, UserRepository repo) {
    return authenticate(ctx, repo, getUsername(ctx));
  }

  private User authenticate(Context ctx, UserRepository repo, String username) {
    AuthenticatableUser user = repo.getAuthenticatableUser(getUsername(ctx));

    if(user == null) {
      return null;
    }

    String authDetails = user.getAuthDetails(this);

    if(authDetails != null && authenticate(authDetails, ctx)) {
      return user.getUser();
    } else {
      return null;
    }
  }

  @Override
  public void updateAuthenticationDetails(Context ctx, UserRepository repo) {
    User currentUser = ctx.sessionAttribute(SessionAttributes.CURRENT_USER);
    User validateAuthentication = authenticate(ctx, repo, currentUser.getUsername());
    if(!currentUser.equals(validateAuthentication)) {
      throw new UnauthorizedResponse();
    }

    repo.setAuthDetails(validateAuthentication, Collections.singletonMap(getDetailsType(), generateNewAuthenticationDetails(ctx)));
  }

  protected abstract String getUsername(Context ctx);
  protected abstract boolean authenticate(String authDetails, Context ctx);
  protected abstract String generateNewAuthenticationDetails(Context ctx);
}
