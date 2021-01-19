package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import io.javalin.http.Context;

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
  
  protected abstract String getUsername(Context ctx);
  protected abstract boolean authenticate(String authDetails, Context ctx);
}
