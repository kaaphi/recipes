package com.kaaphi.recipe.app;

import com.kaaphi.recipe.repo.postgres.PostgresRecipeRepository;
import com.kaaphi.recipe.repo.postgres.PostgresUserRepository;
import com.kaaphi.recipe.users.auth.MemoryLongTermAuthRepo;
import com.kaaphi.velocity.VelocitySLF4JLogChute;
import org.apache.velocity.app.VelocityEngine;

public class ProductionRecipeModule extends RecipeModule {

  public ProductionRecipeModule() {
    super(PostgresUserRepository.class, MemoryLongTermAuthRepo.class, PostgresRecipeRepository.class);  
  }

  @Override
  protected void configureVelocityEngine(VelocityEngine velocityEngine) {
    velocityEngine.setProperty("resource.loader", "class");
    velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngine.setProperty("runtime.log.logsystem", new VelocitySLF4JLogChute());
  }
  
}
