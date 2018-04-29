package com.kaaphi.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Recipe {
  private final UUID id;
  private final String title;
  private final List<Ingredient> ingredients;
  private final String method;
  
  public Recipe(@Nonnull String title, @Nonnull List<Ingredient> ingredients, @Nonnull String method) {
    this(UUID.randomUUID(), title, ingredients, method);
  }
  
  public Recipe(@Nullable UUID id, @Nonnull String title, @Nonnull List<Ingredient> ingredients, @Nonnull String method) {
    this.id = id;
    this.title = title;
    this.ingredients = ingredients;
    this.method = method;
  }
  
  Recipe(MutableRecipe mutableRecipe) {
    this(mutableRecipe.getId(), mutableRecipe.getTitle(), new ArrayList<>(mutableRecipe.getIngredients()), mutableRecipe.getMethod());
  }
  
  public Recipe withId(UUID id) {
    return new Recipe(id, title, ingredients, method);
  }
  
  public UUID getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public List<Ingredient> getIngredients() {
    return Collections.unmodifiableList(ingredients);
  }

  public String getMethod() {
    return method;
  }
  
  public boolean equals(Object o) {
    if(o instanceof Recipe) {
      Recipe that = (Recipe)o;
      return Objects.equals(this.id, that.id)
          && Objects.equals(this.title, that.title)
          && Objects.equals(this.ingredients, that.ingredients)
          && Objects.equals(this.method, that.method);
    } else {
      return false;
    }
  }
  
  public int hashCode() {
    return Objects.hash(id, title, ingredients, method);
  }
  
  public String toString() {
    return String.format("Recipe[id=%s;title=<%s>]", id, title);
  }
}
