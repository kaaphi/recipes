package com.kaaphi.recipe.app;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.put;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.kaaphi.recipe.module.ProductionRecipeModule;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeApp {
  private static final Logger log = LoggerFactory.getLogger(RecipeApp.class);

  
  private final Javalin app;
  private final int listenPort;
    
  @Inject
  public RecipeApp(Javalin app, RecipeController controller, LoginController loginController, @Named("listenPort") String listenPort) {
    this.app = app;
    this.listenPort = Integer.parseInt(listenPort);
    
    app.before(loginController::validateLoggedIn);
    
    app.routes(() -> {
      path(Path.RECIPE_API, () -> {
        get(controller::readAllRecipes);
        post(controller::createRecipe);
        
        path(":id", () -> {
          get(controller::readRecipe);
          put(controller::updateRecipe);
          delete(controller::deleteRecipe);
        });
      });
      
      path("/", () -> {
        get(controller::renderOwnedRecipeList);
      });

      path("/all", () -> {
        get(controller::renderAllRecipeList);
      });

      path("/shared", () -> {
        get(controller::renderSharedRecipeList);
      });

      path("/login", () -> {
        get(loginController::renderLogin);
        post(loginController::handlePost);
      });
      
      path("/logout", () -> {
          get(loginController::handleLogout);
      });
      
      path("/search", () -> {
        get(controller::renderRecipeSearch);
      });
      
      path("recipe/new", () -> {
        get(controller::renderNewRecipe);
      });
      
      path(Path.RECIPE, () -> {
        get(controller::renderRecipe);
                
        path("edit", () -> {
        	get(controller::renderEditRecipe);
        });
        
        path("delete", () -> {
          get(controller::renderDeleteRecipe);
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
