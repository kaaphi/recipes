package com.kaaphi.recipe.app;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.kaaphi.recipe.repo.jsonfile.JsonRecipeRepository;
import com.kaaphi.recipe.repo.jsonfile.UserFileRepository;
import com.kaaphi.recipe.users.auth.MemoryLongTermAuthRepo;
import com.kaaphi.velocity.VelocitySLF4JLogChute;
import javax.sql.DataSource;
import org.apache.velocity.app.VelocityEngine;
import org.postgresql.ds.PGSimpleDataSource;

public class ProductionRecipeModule extends RecipeModule {

  public ProductionRecipeModule() {
    super(UserFileRepository.class, MemoryLongTermAuthRepo.class, JsonRecipeRepository.class);  
  }


  @Override
  protected void configureVelocityEngine(VelocityEngine velocityEngine) {
    velocityEngine.setProperty("resource.loader", "class");
    velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngine.setProperty("runtime.log.logsystem", new VelocitySLF4JLogChute());
  }
  
}
