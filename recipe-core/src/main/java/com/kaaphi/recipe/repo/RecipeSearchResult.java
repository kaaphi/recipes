package com.kaaphi.recipe.repo;

import java.util.Objects;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.repo.RecipeSearch.StringMatch;

public class RecipeSearchResult implements Comparable<RecipeSearchResult> {
  private final RecipeBookEntry entry;
  private final double searchScore;
  private final StringMatch match;
  private final boolean titleMatch;
  
  public RecipeSearchResult(RecipeBookEntry entry, double searchScore, StringMatch match, boolean isTitleMatch) {
    this.entry = entry;
    this.searchScore = searchScore;
    this.match = match;
    this.titleMatch = isTitleMatch;
  }

  public RecipeBookEntry getEntry() {
    return entry;
  }
  
  public double getSearchScore() {
    return searchScore;
  }
  
  public StringMatch getMatch() {
    return match;
  }
  
  public boolean isTitleMatch() {
    return titleMatch;
  }

  @Override
  public int compareTo(RecipeSearchResult o) {
    int result = Double.compare(o.searchScore, this.searchScore);
    
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
