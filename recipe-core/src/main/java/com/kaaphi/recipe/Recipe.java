package com.kaaphi.recipe;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Recipe {
  private final UUID id;
  private final String title;
  private final List<Ingredient> ingredients;
  private final String method;
  private final Optional<Instant> serializedTime;

  public Recipe(String title, List<Ingredient> ingredients, String method, Optional<Instant> serializedTime) {
    this(UUID.randomUUID(), title, ingredients, method, serializedTime);
  }
  
  public Recipe(UUID id, String title, List<Ingredient> ingredients, String method, Optional<Instant> serializedTime) {
    this.id = id;
    this.title = title;
    this.ingredients = ingredients;
    this.method = method;
    this.serializedTime = serializedTime;
  }
  
  Recipe(MutableRecipe mutableRecipe) {
    this(mutableRecipe.getId(), mutableRecipe.getTitle(), new ArrayList<>(mutableRecipe.getIngredients()), mutableRecipe.getMethod(), Optional.empty());
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

  public Optional<Instant> getSerializedTime() {
    return serializedTime;
  }
}
