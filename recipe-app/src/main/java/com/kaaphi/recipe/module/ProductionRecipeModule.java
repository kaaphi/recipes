package com.kaaphi.recipe.module;

import com.google.inject.util.Modules;

public class ProductionRecipeModule {
  public static com.google.inject.Module getProductionModule() {
    return Modules.override(
        new RecipeModule(), 
        new VelocityModule())
        .with(new DockerSecretModule());
  }
}
