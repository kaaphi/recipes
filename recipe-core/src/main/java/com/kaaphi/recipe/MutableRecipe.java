package com.kaaphi.recipe;

import java.util.List;
import java.util.UUID;

public class MutableRecipe {
  private final Recipe source;
  private String title;
  private List<Ingredient> ingredients;
  private String method;
  
  public MutableRecipe(Recipe source) {
    this.source = source;
  }
  
  public UUID getId() {
    return source.getId();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public Recipe getSource() {
    return source;
  }
  
  public boolean isModified() {
    return !title.equals(source.getTitle()) 
        || !ingredients.equals(source.getIngredients())
        || !method.equals(source.getMethod());
  }
  
  public Recipe build() {
    if(isModified()) {
      return new Recipe(this);
    } else {
      return source;
    }
  }
}
