package com.kaaphi.recipe;

import java.util.Collections;
import java.util.List;
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
}
