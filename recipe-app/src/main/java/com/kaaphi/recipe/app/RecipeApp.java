package com.kaaphi.recipe.app;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.prefixPath;
import static io.javalin.apibuilder.ApiBuilder.put;
import static io.javalin.core.security.SecurityUtil.roles;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.kaaphi.recipe.module.ProductionRecipeModule;
import com.kaaphi.recipe.users.UserRole;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeApp {
  private static final Logger log = LoggerFactory.getLogger(RecipeApp.class);

  
  private final Javalin app;
  private final int listenPort;
    
  @Inject
  public RecipeApp(Javalin app, RecipeController controller, LoginController loginController, UserController userController, @Named("listenPort") String listenPort) {
    this.app = app;
    this.listenPort = Integer.parseInt(listenPort);
    app.config.accessManager((handler, ctx, permittedRoles) -> {
      //if there are no roles, then the resource is available to anyone
      if(loginController.validateLoggedIn(ctx, permittedRoles)) {
        handler.handle(ctx);
      } else if (ctx.path().startsWith("/api")) {
        throw new UnauthorizedResponse();
      } else {
        //redirect to login
        ctx.redirect("/login");
      }
    });
    //app.before(loginController::validateLoggedIn);
    
    app.routes(() -> {
      path(Path.RECIPE_API, () -> {
        get(controller::readAllRecipes, roles(UserRole.USER));
        post(controller::createRecipe, roles(UserRole.USER));
        
        path(":id", () -> {
          get(controller::readRecipe, roles(UserRole.USER));
          put(controller::updateRecipe, roles(UserRole.USER));
          delete(controller::deleteRecipe, roles(UserRole.USER));
        });
      });

      crud(Path.USER_API, userController, roles(UserRole.ADMIN));
      path(Path.USER_SHARE_API, () -> {
        get(prefixPath(":username"), userController::getShares, roles(UserRole.ADMIN));
        path(":fromUsername/:toUsername", () -> {
          post(userController::addShare, roles(UserRole.ADMIN));
          delete(userController::addShare, roles(UserRole.ADMIN));
        });
      });
      
      path("/", () -> {
        get(controller::renderOwnedRecipeList, roles(UserRole.USER));
      });

      path("/all", () -> {
        get(controller::renderAllRecipeList, roles(UserRole.USER));
      });

      path("/shared", () -> {
        get(controller::renderSharedRecipeList, roles(UserRole.USER));
      });

      path("/login", () -> {
        get(loginController::renderLogin);
        post(loginController::handlePost);
      });
      
      path("/logout", () -> {
          get(loginController::handleLogout, roles(UserRole.USER,UserRole.ADMIN));
      });
      
      path("/search", () -> {
        get(controller::renderRecipeSearch, roles(UserRole.USER));
      });
      
      path("recipe/new", () -> {
        get(controller::renderNewRecipe, roles(UserRole.USER));
      });
      
      path(Path.RECIPE, () -> {
        get(controller::renderRecipe, roles(UserRole.USER));
                
        path("edit", () -> {
        	get(controller::renderEditRecipe, roles(UserRole.USER));
        });
        
        path("delete", () -> {
          get(controller::renderDeleteRecipe, roles(UserRole.USER));
      });

      });
    });
    
    log.info("App initialized.");
  }
  
  public void start() {
    app.start(listenPort);
    log.info("App started.");
  }
  
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(ProductionRecipeModule.getProductionModule());
    
    RecipeApp app = injector.getInstance(RecipeApp.class);
    
    app.start();
  }
}
