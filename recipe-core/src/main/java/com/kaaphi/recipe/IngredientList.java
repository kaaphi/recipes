package com.kaaphi.recipe;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class IngredientList {
  private final String name;
  private final List<Ingredient> ingredients;
  
  public IngredientList(String name, @Nonnull List<Ingredient> ingredients) {
    super();
    this.name = name;
    this.ingredients = ingredients;
  }
  
  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }
  
  public List<Ingredient> getIngredients() {
    return Collections.unmodifiableList(ingredients);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IngredientList that = (IngredientList) o;
    return Objects.equals(name, that.name) && ingredients.equals(that.ingredients);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, ingredients);
  }
}
