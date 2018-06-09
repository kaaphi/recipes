package com.kaaphi.recipe.app;

import static io.javalin.ApiBuilder.delete;
import static io.javalin.ApiBuilder.get;
import static io.javalin.ApiBuilder.path;
import static io.javalin.ApiBuilder.post;
import static io.javalin.ApiBuilder.put;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeApp {
  private static final Logger log = LoggerFactory.getLogger(RecipeApp.class);

  
  private final Javalin app;
    
  @Inject
  public RecipeApp(Javalin app, RecipeController controller, LoginController loginController) {
    this.app = app;
    
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
        get(controller::renderRecipeList);
      });
      
      path("/login", () -> {
        get(loginController::renderLogin);
        post(loginController::handlePost);
      });
      
      path("/logout", () -> {
          get(loginController::handleLogout);
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
    app.start();   
    log.info("App started.");
  }
  
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ProductionRecipeModule());
    
    RecipeApp app = injector.getInstance(RecipeApp.class);
    
    app.start();
  }
}
