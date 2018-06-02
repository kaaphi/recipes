package com.kaaphi.recipe.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaaphi.recipe.repo.jsonfile.JsonRecipeRepository;
import com.kaaphi.recipe.repo.postgres.PostgresRecipeRepository;
import com.kaaphi.recipe.repo.postgres.PostgresUserRepository;
import com.kaaphi.recipe.users.auth.MemoryLongTermAuthRepo;
import org.apache.velocity.app.VelocityEngine;

public class RunDevRecipeApp {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new DevRecipeModule());
    
    RecipeApp app = injector.getInstance(RecipeApp.class);
    
    app.start();
  }
  
  public static class DevRecipeModule extends RecipeModule {
    public DevRecipeModule() {
      super(PostgresUserRepository.class, MemoryLongTermAuthRepo.class, PostgresRecipeRepository.class);  
    }
    
    @Override
    protected void configureVelocityEngine(VelocityEngine velocityEngine) {
      velocityEngine.setProperty("resource.loader", "file");  
      velocityEngine.setProperty("velocimacro.library.autoreload", "true");
      velocityEngine.setProperty("file.resource.loader.cache", "false");
      velocityEngine.setProperty("file.resource.loader.path", "./src/main/resources");
    }
  }
}
