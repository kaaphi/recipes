package com.kaaphi.recipe.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.kaaphi.recipe.module.RecipeModule;
import com.kaaphi.recipe.module.VelocityModule;
import com.kaaphi.recipe.users.auth.LongTermAuthRepository;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.velocity.app.VelocityEngine;

public class RunDevRecipeApp {
  public static void main(String[] args) {

    Injector injector = Guice.createInjector(Modules.override(
        new RecipeModule(),
        new DevVelocityModule()
        ).with(binder -> {
          binder.bind(Path.class)
              .annotatedWith(Names.named("longTermAuthFile"))
              .toInstance(Paths.get(System.getProperty("java.io.tmpdir", ".")).resolve("recipe_auth.cache"));
          binder.bind(LongTermAuthRepository.class).to(SimpleFileLongTermAuthRepo.class);
        })
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
