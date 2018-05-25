package com.kaaphi.recipe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class Recipe {
  private final String title;
  private final List<Ingredient> ingredients;
  private final String method;
  private final List<String> sources;
  
  public Recipe(@Nonnull String title, @Nonnull List<Ingredient> ingredients, @Nonnull String method, @Nonnull List<String> sources) {
    this.title = title;
    this.ingredients = ingredients;
    this.method = method;
    this.sources = sources;
  }
  
  public String getTitle() {
    return title;
  }

  public List<Ingredient> getIngredients() {
    return Collections.unmodifiableList(ingredients);
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
          && Objects.equals(this.ingredients, that.ingredients)
          && Objects.equals(this.method, that.method)
          && Objects.equals(this.sources, that.sources);
    } else {
      return false;
    }
  }
  
  public int hashCode() {
    return Objects.hash(title, ingredients, method, sources);
  }
  
  public String toString() {
    return String.format("Recipe[%s]", title);
  }
}
