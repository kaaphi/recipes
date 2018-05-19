package com.kaaphi.recipe.txtformat;

import com.kaaphi.recipe.Ingredient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientParser {
  private static final Pattern INGREDIENT_PATTERN = Pattern.compile("((([0-9\\-/ .]+)(\\s*(\\w*\\.?)))\\s+)?(.+)");
  
  public Ingredient fromString(String line) {
    Matcher m = INGREDIENT_PATTERN.matcher(line);
    if(m.matches()) {
      String quantity = m.group(2);
      String name = m.group(6);
      return new Ingredient(name, quantity);
    } else {
      return new Ingredient(line);      
    }    
  }
}
