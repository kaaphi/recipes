package com.kaaphi.recipe.repo;

import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.StoredRecipe;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface RecipeRepository {
  public Set<StoredRecipe> getAll();
  public StoredRecipe get(UUID id);
  public default void delete(UUID id) {
    deleteById(Collections.singleton(id));
  }
  public default void delete(StoredRecipe recipe) {
    delete(recipe.getId());
  }
  public void deleteById(Set<UUID> toRemove);
  public default void delete(Set<StoredRecipe> toRemove) {
    deleteById(toRemove.stream().map(StoredRecipe::getId).collect(Collectors.toSet()));
  }
  public void saveAll(Set<StoredRecipe> recipes);
  public default void save(StoredRecipe recipe) {
    saveAll(Collections.singleton(recipe));
  }
  public default StoredRecipe save(UUID id, Recipe recipe) {
    StoredRecipe result = new StoredRecipe(id, recipe, Instant.now(), Instant.now());
    save(result);
    return result;
  }
}
