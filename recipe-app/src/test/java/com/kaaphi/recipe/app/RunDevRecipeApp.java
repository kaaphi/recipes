package com.kaaphi.recipe.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaaphi.recipe.app.RecipeApp;
import com.kaaphi.recipe.app.RecipeModule;
import org.apache.velocity.app.VelocityEngine;

public class RunDevRecipeApp {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new RecipeModule() {
      @Override
      protected void configureVelocityEngine(VelocityEngine velocityEngine) {
        velocityEngine.setProperty("resource.loader", "file");  
        velocityEngine.setProperty("velocimacro.library.autoreload", "true");
        velocityEngine.setProperty("file.resource.loader.cache", "false");
        velocityEngine.setProperty("file.resource.loader.path", "./src/main/resources");
      }
       
    });
    
    RecipeApp app = injector.getInstance(RecipeApp.class);
    
    app.start();
  }
}
