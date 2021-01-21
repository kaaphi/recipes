package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.app.SessionAttributes;
import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import java.util.Collections;
import java.util.function.Function;

public abstract class AbstractAuthenticationMethod implements AuthenticationMethod {

  @Override
  public User authenticate(Context ctx, UserRepository repo) {
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

  private User authenticate(Context ctx, UserRepository repo, Function<Context, String> usernameFunction) {
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
    User validateAuthentication = authenticate(ctx, repo, c -> c.<User>sessionAttribute(SessionAttributes.CURRENT_USER).getUsername());
    if(validateAuthentication == null) {
      throw new UnauthorizedResponse();
    }

    repo.setAuthDetails(validateAuthentication, Collections.singletonMap(getDetailsType(), generateNewAuthenticationDetails(ctx)));
  }

  protected abstract String getUsername(Context ctx);
  protected abstract boolean authenticate(String authDetails, Context ctx);
  protected abstract String generateNewAuthenticationDetails(Context ctx);
}
