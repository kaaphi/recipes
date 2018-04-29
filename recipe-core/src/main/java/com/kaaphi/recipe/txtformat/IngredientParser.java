package com.kaaphi.recipe.txtformat;

import com.kaaphi.recipe.Ingredient;

public class IngredientParser {
  public Ingredient fromString(String line) {
    return new Ingredient(line);
  }
}
