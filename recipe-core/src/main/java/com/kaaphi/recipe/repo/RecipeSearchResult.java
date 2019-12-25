package com.kaaphi.recipe.repo;

import java.util.Objects;
import com.kaaphi.recipe.RecipeBookEntry;

public class RecipeSearchResult implements Comparable<RecipeSearchResult> {
  private final RecipeBookEntry entry;
  private final int searchScore;
  
  public RecipeSearchResult(RecipeBookEntry entry, int searchScore) {
    this.entry = entry;
    this.searchScore = searchScore;
  }

  public RecipeBookEntry getEntry() {
    return entry;
  }
  
  public int getSearchScore() {
    return searchScore;
  }

  @Override
  public int compareTo(RecipeSearchResult o) {
    int result = Integer.compare(o.searchScore, this.searchScore);
    
    if(result == 0) {
      result = this.entry.getRecipe().getTitle().compareTo(o.entry.getRecipe().getTitle());
    }

    return result;
  }

  @Override
  public int hashCode() {
    return Objects.hash(entry, searchScore);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof RecipeSearchResult)) {
      return false;
    }
    RecipeSearchResult other = (RecipeSearchResult) obj;
    return Objects.equals(entry, other.entry) && searchScore == other.searchScore;
  }
  
  
}
