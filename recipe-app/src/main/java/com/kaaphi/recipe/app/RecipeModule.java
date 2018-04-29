package com.kaaphi.recipe.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.jsonfile.JsonRecipeRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class RecipeModule extends AbstractModule {

  @Override
  protected void configure() {
    Names.bindProperties(binder(), loadProperties());
    
    bind(RecipeRepository.class).to(JsonRecipeRepository.class);
  }
  
  @Provides Gson provideGson() {
    return new GsonBuilder()
        .setPrettyPrinting()
        .create();
  }
  
  private Properties loadProperties() {
    Properties props = new Properties();
    try(InputStream in = Files.newInputStream(Paths.get("./localConfig/config.properties"))) {
      props.load(in);
    } catch (IOException e) {
      throw new Error(e);
    }
    return props;
  }
  
}
