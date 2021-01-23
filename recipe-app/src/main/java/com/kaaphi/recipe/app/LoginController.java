package com.kaaphi.recipe.app;

import static com.kaaphi.recipe.app.SessionAttributes.CURRENT_ROLES;
import static com.kaaphi.recipe.app.SessionAttributes.CURRENT_USER;

import com.google.inject.Inject;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.UserRole;
import com.kaaphi.recipe.users.auth.AuthenticationMethod;
import com.kaaphi.recipe.users.auth.AuthenticationMethods;
import com.kaaphi.recipe.users.auth.BasicAuthentication;
import com.kaaphi.recipe.users.auth.PasswordPostAuthentication;
import io.javalin.core.security.Role;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
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
    if(ctx.sessionAttribute(CURRENT_USER) != null) {
      //already logged in, redirect to root
      ctx.redirect("/");
    } else {
      ctx.render("/login.html", new HashMap<>());
    }
  }

  public void renderChangePassword(Context ctx) {
    ctx.render("/change_password.html", new HashMap<>());
  }


  public void accessManager(Handler handler, Context ctx, Set<Role> permittedRoles)
      throws Exception {
    final boolean userIsLoggedIn;

    //always allow empty set or anonymous, early return
    if(permittedRoles.isEmpty() || permittedRoles.contains(UserRole.ANONYMOUS)) {
      handler.handle(ctx);
      return;
    }
    else if(ctx.sessionAttribute(CURRENT_USER) != null) {
      //already logged in
      userIsLoggedIn = true;
    }
    else {
      log.trace("No logged in user. Path {} Matched Path {}", ctx.path(), ctx.matchedPath());
      //check for long-term auth token
      User user = longTermAuthController.validateLongTermAuth(ctx);
      if(user != null) {
        cacheUserAndRolesInSession(user, ctx);
        userIsLoggedIn = true;
      } else if(ctx.basicAuthCredentialsExist()) {
        log.trace("Doing basic auth.");
        userIsLoggedIn = doAuth(new BasicAuthentication(), ctx);
      } else {
        userIsLoggedIn = false;
      }
    }

    if(userIsLoggedIn) {
      //check roles
      Set<Role> userRoles = ctx.sessionAttribute(CURRENT_ROLES);
      if(userRoles.stream().anyMatch(permittedRoles::contains)) {
        handler.handle(ctx);
      } else {
        throw new ForbiddenResponse();
      }
    } else if(ctx.path().startsWith("/api")) {
      throw new UnauthorizedResponse();
    } else {
      //redirect to login
      ctx.redirect("/login");
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

  public void handlePasswordChange(Context ctx) throws IOException {
    AuthenticationMethod authType = Optional.ofNullable(ctx.formParam("method"))
        .map(AuthenticationMethods::getAuthenticationMethod)
        .orElse(new PasswordPostAuthentication());

    authType.updateAuthenticationDetails(ctx, repo);

    ctx.redirect("/", 303);
  }
  
  private boolean doAuth(AuthenticationMethod method, Context ctx) throws IOException {
    User user = method.authenticate(ctx, repo);

    if(user == null) {
      return false;
    } else {
      cacheUserAndRolesInSession(user, ctx);
      longTermAuthController.saveNewSession(user, ctx);
      return true;
    }
  }

  private void cacheUserAndRolesInSession(User user, Context ctx) {
    ctx.sessionAttribute(CURRENT_USER, user);
    ctx.sessionAttribute(CURRENT_ROLES, repo.getRolesForUser(user));
  }

}
