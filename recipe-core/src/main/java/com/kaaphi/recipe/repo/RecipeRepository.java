package com.kaaphi.recipe.repo;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.kaaphi.recipe.RecipeBookEntry;

public interface RecipeRepository {
  public Set<RecipeBookEntry> getAll();
  public RecipeBookEntry get(UUID id);
  public default void delete(UUID id) {
    deleteById(Collections.singleton(id));
  }
  public default void delete(RecipeBookEntry recipe) {
    delete(recipe.getId());
  }
  public void deleteById(Set<UUID> toRemove);
  public default void delete(Set<RecipeBookEntry> toRemove) {
    deleteById(toRemove.stream().map(RecipeBookEntry::getId).collect(Collectors.toSet()));
  }
  public void saveAll(Set<RecipeBookEntry> recipes);
  public default void save(RecipeBookEntry recipe) {
    saveAll(Collections.singleton(recipe));
  }
}
