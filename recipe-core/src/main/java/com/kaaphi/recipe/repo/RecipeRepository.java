package com.kaaphi.recipe.repo;

import com.kaaphi.recipe.Recipe;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface RecipeRepository {
  public Set<Recipe> getAll();
  public void deleteById(Set<UUID> toRemove);
  public default void delete(Set<Recipe> toRemove) {
    deleteById(toRemove.stream().map(Recipe::getId).collect(Collectors.toSet()));
  }
  public void saveAll(Set<Recipe> recipes);
  public default void save(Recipe recipe) {
    saveAll(Collections.singleton(recipe));
  }
}
