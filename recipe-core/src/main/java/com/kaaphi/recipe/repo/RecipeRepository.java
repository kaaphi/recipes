package com.kaaphi.recipe.repo;

import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.users.User;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface RecipeRepository {
  enum RecipeCategory {
    ALL,
    OWNED,
    SHARED;
    
    public static Optional<RecipeCategory> optionalValueOf(String str) {
      return str == null ? Optional.empty() : Stream.of(values())
      .filter(c -> c.name().equals(str))
      .findAny();
    }
  }

  Set<RecipeBookEntry> getAll(boolean includeArchive);

  default Set<RecipeBookEntry> getAll() {
    return getAll(false);
  }
  default Set<RecipeBookEntry> getOwned() {
    return getAll().stream()
        .filter(r -> r.getOwner().equals(getUser()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
  default Set<RecipeBookEntry> getShared() {
    return getAll().stream()
        .filter(r -> !r.getOwner().equals(getUser()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
  default Stream<RecipeBookEntry> getCategory(RecipeCategory category) {
    Optional<Predicate<RecipeBookEntry>> filter;
    switch(category) {
      case OWNED:
        filter = Optional.of(r -> r.getOwner().equals(getUser()));
        break;
        
      case SHARED:
        filter = Optional.of(r -> !r.getOwner().equals(getUser()));
        break;
        
      case ALL:
      default:
        filter = Optional.empty();        
    }
    
    Stream<RecipeBookEntry> allRecipeBookEntries = getAll().stream();
    return filter.map(allRecipeBookEntries::filter).orElse(allRecipeBookEntries);
  }
  
  default Set<RecipeSearchResult> searchRecipes(RecipeCategory category, boolean includeArchive, String searchString) {
    RecipeSearch search = new RecipeSearch(searchString);
    
    return getCategory(category)
    .map(search::createResultForRecipe)
    .filter(Objects::nonNull)
    .sorted()
    .collect(Collectors.toCollection(LinkedHashSet::new));    
  }
  
  RecipeBookEntry get(UUID id);
  default void delete(UUID id) {
    deleteById(Collections.singleton(id));
  }
  default void delete(RecipeBookEntry recipe) {
    delete(recipe.getId());
  }
  void deleteById(Set<UUID> toRemove);
  void archiveById(Set<UUID> toArchive);
  void unarchiveById(Set<UUID> toUnarchive);
  default void delete(Set<RecipeBookEntry> toRemove) {
    deleteById(toRemove.stream().map(RecipeBookEntry::getId).collect(Collectors.toSet()));
  }
  void saveAll(Set<RecipeBookEntry> recipes);
  default void save(RecipeBookEntry recipe) {
    saveAll(Collections.singleton(recipe));
  }
  User getUser();
}
