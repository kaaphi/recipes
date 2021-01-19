package com.kaaphi.recipe.app;

import static com.kaaphi.recipe.app.SessionAttributes.CURRENT_USER;

import com.google.inject.Inject;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.AuthenticationMethod;
import com.kaaphi.recipe.users.auth.AuthenticationMethods;
import com.kaaphi.recipe.users.auth.BasicAuthentication;
import com.kaaphi.recipe.users.auth.PasswordPostAuthentication;
import io.javalin.http.Context;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
  private static final Logger log = LoggerFactory.getLogger(LoginController.class);

  private UserRepository repo;
  private LongTermAuthController longTermAuthController;
  
  @Inject
  public LoginController(UserRepository repo, LongTermAuthController longTermAuthController) {
    this.repo = repo;
    this.longTermAuthController = longTermAuthController;
  }
  
  public void renderLogin(Context ctx) {
    ctx.render("/login.html", new HashMap<>());
  }
  
  public void validateLoggedIn(Context ctx) throws IOException {
    //always allow static resources
    if(!"/".equals(ctx.path()) && ClassLoader.getSystemResource("static" + ctx.path()) != null) {
      return;
    }
    
    if(ctx.sessionAttribute(CURRENT_USER) == null) {
      log.trace("No logged in user. Path {} Matched Path {}", ctx.path(), ctx.matchedPath());
      //check for long-term auth token
      User user = longTermAuthController.validateLongTermAuth(ctx);
      if(user != null) {
        ctx.sessionAttribute(CURRENT_USER, user);
      } else if(ctx.basicAuthCredentials() != null) {
        log.trace("Doing basic auth.");
        if(!doAuth(new BasicAuthentication(), ctx)) {
          ctx.res.sendError(401);
        }
      } else if(!"/login".equals(ctx.path())) {      
        ctx.redirect("/login");
      }
    }
    
    if(ctx.sessionAttribute(CURRENT_USER) != null && "/login".equals(ctx.path())) {
      //logged in now, redirect to main page
      ctx.redirect("/");
    }
  }
  
  public void handleLogout(Context ctx) {
    ctx.req.getSession().removeAttribute(CURRENT_USER);
    longTermAuthController.removeExisingSession(ctx);
    ctx.redirect("/login");
  }
  
  public void handlePost(Context ctx) throws IOException {
    AuthenticationMethod authType = Optional.ofNullable(ctx.formParam("method"))
        .map(AuthenticationMethods::getAuthenticationMethod)
        .orElse(new PasswordPostAuthentication());
    
    if(doAuth(authType, ctx)) {
      ctx.redirect("/", 303);
    } else {
      ctx.res.sendError(401);
    }
  }
  
  private boolean doAuth(AuthenticationMethod method, Context ctx) throws IOException {
    User user = method.authenticate(ctx, repo);
    
    if(user == null) {
      return false;
    } else {
      ctx.sessionAttribute(CURRENT_USER, user);
      longTermAuthController.saveNewSession(user, ctx);
      return true;
    }
  }

}
