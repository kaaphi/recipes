package com.kaaphi.recipe.app;

import static com.kaaphi.recipe.app.SessionAttributes.CURRENT_USER;
import com.google.inject.Inject;
import com.kaaphi.recipe.users.AuthenticationMethod;
import com.kaaphi.recipe.users.AuthenticationMethods;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.BasicAuthentication;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;
import com.kaaphi.recipe.users.auth.PasswordPostAuthentication;
import io.javalin.Context;
import java.io.IOException;
import java.util.Optional;

public class LoginController {

  private UserRepository repo;
  
  @Inject
  public LoginController(UserRepository repo) {
    this.repo = repo;
  }
  
  public void validateLoggedIn(Context ctx) throws IOException {
    if(ctx.sessionAttribute(CURRENT_USER) == null) {
      if(ctx.basicAuthCredentials() != null) {
        doAuth(new BasicAuthentication(), ctx);
      } else {      
        ctx.response().addHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"");
        ctx.response().sendError(401);
      }
    }
  }
  
  public void handlePost(Context ctx) throws IOException {
    AuthenticationMethod authType = Optional.ofNullable(ctx.formParam("method"))
        .map(AuthenticationMethods::getAuthenticationMethod)
        .orElse(new PasswordPostAuthentication());
    
    doAuth(authType, ctx);
  }
  
  private void doAuth(AuthenticationMethod method, Context ctx) throws IOException {
    User user = method.authenticate(ctx, repo);
    
    if(user == null) {
      ctx.response().sendError(401);
    } else {
      ctx.sessionAttribute(CURRENT_USER, user);
    }
  }

}
