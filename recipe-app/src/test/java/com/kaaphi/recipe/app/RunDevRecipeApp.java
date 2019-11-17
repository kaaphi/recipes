package com.kaaphi.recipe.app;

import org.apache.velocity.app.VelocityEngine;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaaphi.recipe.module.RecipeModule;
import com.kaaphi.recipe.module.VelocityModule;

public class RunDevRecipeApp {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(
        new RecipeModule(),
        new DevVelocityModule()
        );
    
    RecipeApp app = injector.getInstance(RecipeApp.class);
    
    app.start();
  }
  
  public static class DevVelocityModule extends VelocityModule {
    @Override
    protected void configureVelocityEngine(VelocityEngine velocityEngine) {
      velocityEngine.setProperty("resource.loader", "file");  
      velocityEngine.setProperty("velocimacro.library.autoreload", "true");
      velocityEngine.setProperty("file.resource.loader.cache", "false");
      velocityEngine.setProperty("file.resource.loader.path", "./src/main/resources");
    }
  }
}
