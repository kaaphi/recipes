package com.kaaphi.recipe;

import com.google.common.collect.BiMap;
import java.util.Set;
import java.util.UUID;

public class RecipeBook {
  private BiMap<UUID, Recipe> recipes;
  
  public Set<Recipe> getRecipes() {
    return recipes.values();
  }
  
  public Recipe put(Recipe recipe) {
    return recipes.put(recipe.getId(), recipe);
  }
  
  public Recipe remove(UUID id) {
    return recipes.remove(id);
  }
}
