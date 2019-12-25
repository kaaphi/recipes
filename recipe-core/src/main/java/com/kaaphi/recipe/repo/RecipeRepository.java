package com.kaaphi.recipe.repo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.IngredientList;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.users.User;

public interface RecipeRepository {
  public static enum RecipeCategory {
    ALL,
    OWNED,
    SHARED
  }
  
  public Set<RecipeBookEntry> getAll();
  public default Set<RecipeBookEntry> getOwned() {
    return getAll().stream()
        .filter(r -> r.getOwner().equals(getUser()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
  public default Set<RecipeBookEntry> getShared() {
    return getAll().stream()
        .filter(r -> !r.getOwner().equals(getUser()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
  public default Stream<RecipeBookEntry> getCategory(RecipeCategory category) {
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
  
  public default Set<RecipeBookEntry> searchRecipes(RecipeCategory category, String search) {
    Predicate<String> searchMatches = s -> s.toLowerCase().contains(search.toLowerCase());
    
    return getCategory(category)
    .map(r -> createRecipeSearchResult(r, searchMatches))
    .filter(Objects::nonNull)
    .sorted()
    .map(RecipeSearchResult::getEntry)
    .collect(Collectors.toCollection(LinkedHashSet::new));    
  }
  
  private static RecipeSearchResult createRecipeSearchResult(RecipeBookEntry entry, Predicate<String> search) {
    int score = 0;
    if(search.test(entry.getRecipe().getTitle())) {
      score += 4;
    }
    if (entry.getRecipe().getIngredientLists().stream()
        .map(IngredientList::getIngredients)
        .flatMap(List::stream)
        .map(Ingredient::getName)
        .anyMatch(search)) {
      score += 3;
    }
    if(search.test(entry.getRecipe().getMethod())) {
      score += 2;
    }
    if(entry.getRecipe().getSources().stream().anyMatch(search)) {
      score += 1;
    }
    
    return score > 0 ? new RecipeSearchResult(entry, score) : null;
  }
  
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
  public User getUser();
}
