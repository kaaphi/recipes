package com.kaaphi.recipe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class Recipe {
  private final String title;
  private final List<IngredientList> ingredientLists;
  private final String method;
  private final List<String> sources;
  
  public Recipe(@Nonnull String title, @Nonnull List<IngredientList> ingredientLists, @Nonnull String method, @Nonnull List<String> sources) {
    this.title = title;
    this.ingredientLists = ingredientLists;
    this.method = method;
    this.sources = sources;
  }
  
  public String getTitle() {
    return title;
  }

  public List<IngredientList> getIngredientLists() {
    return Collections.unmodifiableList(ingredientLists);
  }
  
  public List<String> getSources() {
    return Collections.unmodifiableList(sources);
  }

  public String getMethod() {
    return method;
  }
  
  public boolean equals(Object o) {
    if(o instanceof Recipe) {
      Recipe that = (Recipe)o;
      return Objects.equals(this.title, that.title)
          && Objects.equals(this.ingredientLists, that.ingredientLists)
          && Objects.equals(this.method, that.method)
          && Objects.equals(this.sources, that.sources);
    } else {
      return false;
    }
  }
  
  public int hashCode() {
    return Objects.hash(title, ingredientLists, method, sources);
  }
  
  public String toString() {
    return String.format("Recipe[%s]", title);
  }
}
