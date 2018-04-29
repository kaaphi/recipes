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

public class RecipeApp {
  private final Javalin app;
    
  @Inject
  public RecipeApp(RecipeController controller) {
    app = Javalin.create()
        .port(7000);
    
    app.routes(() -> {
      path(Path.RECIPES, () -> {
        get(controller::readAllRecipes);
        post(controller::createRecipe);
        
        path(":id", () -> {
          get(controller::readRecipe);
          put(controller::updateRecipe);
          delete(controller::deleteRecipe);
          
          path("render", () -> {
            get(controller::render);
          });
        });
      });
    });
  }
  
  public void start() {
    app.start();    
  }
  
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new RecipeModule());
    
    RecipeApp app = injector.getInstance(RecipeApp.class);
    
    app.start();
  }
}
