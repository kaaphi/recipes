package com.kaaphi.recipe.app.renderer;

import com.github.rjeschke.txtmark.Processor;
import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.IngredientList;
import com.kaaphi.recipe.Recipe;

public class RecipeMarkdownProcessor {
  public static String process(Recipe recipe) {
    return Processor.process(buildMarkdown(recipe));    
  }
  
  private static String buildMarkdown(Recipe recipe) {
    StringBuilder sb = new StringBuilder()
        .append("# ").append(recipe.getTitle()).append("\n")
        .append("## Ingredients\n")
        ;
    
    recipe.getIngredientLists()
    .forEach(il -> ingredientListToMarkdown(sb, il));
    
    sb.append("\n## Method\n");
    
    sb.append(recipe.getMethod());
    
    return sb.toString();
  }
  
  private static void ingredientListToMarkdown(StringBuilder sb, IngredientList list) {
    list.getName().ifPresent(name -> sb.append("\n### ").append(name).append("\n"));
    
    list.getIngredients().stream()
    .map(RecipeMarkdownProcessor::ingredientToMarkdown)
    .forEach(sb::append);
  }
  
  private static String ingredientToMarkdown(Ingredient i) {
    if(i.getQuantity().isPresent()) {
      return "* " + i.getQuantity().get() + " " + i.getName() + "\n";
    } else {
      return "* " + i.getName() + "\n";
    }
  }
}
