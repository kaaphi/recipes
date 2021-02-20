package com.kaaphi.recipe.repo;

import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.users.User;
import java.util.Arrays;
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
  enum RecipeScope {
    ALL,
    OWNED,
    SHARED,
    ARCHIVED;
    
    public static Optional<RecipeScope> optionalValueOf(String str) {
      return str == null ? Optional.empty() : Stream.of(values())
      .filter(c -> c.name().equals(str))
      .findAny();
    }
  }

  Set<RecipeBookEntry> getAll();

  default Set<RecipeBookEntry> getRecipeSet(RecipeScope scope) {
    return getRecipes(scope)
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  default Stream<RecipeBookEntry> getRecipes(RecipeScope... scope) {
    Optional<Predicate<RecipeBookEntry>> filter = Arrays.stream(scope)
        .<Predicate<RecipeBookEntry>>map(s -> {
              switch (s) {
                case OWNED:
                  return r -> r.getOwner().equals(getUser()) && !r.isArchived();

                case SHARED:
                  return r -> !r.getOwner().equals(getUser());

                case ARCHIVED:
                  return RecipeBookEntry::isArchived;

                case ALL:
                default:
                  return __ -> true;
              }
            })
        .reduce(Predicate::or);
    
    Stream<RecipeBookEntry> allRecipeBookEntries = getAll().stream();
    return filter.map(allRecipeBookEntries::filter).orElse(allRecipeBookEntries);
  }
  
  default Set<RecipeSearchResult> searchRecipes(RecipeScope scope, String searchString) {
    RecipeSearch search = new RecipeSearch(searchString);
    
    return getRecipes(scope)
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
