package com.kaaphi.recipe.app.renderer;

import com.github.rjeschke.txtmark.Processor;
import com.kaaphi.recipe.Ingredient;
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
    
    recipe.getIngredients().stream()
    .map(RecipeMarkdownProcessor::ingredientToMarkdown)
    .forEach(sb::append);
    
    sb.append("\n## Method\n");
    
    sb.append(recipe.getMethod());
    
    return sb.toString();
  }
  
  private static String ingredientToMarkdown(Ingredient i) {
    if(i.getQuantity().isPresent()) {
      return "* " + i.getQuantity().get() + " " + i.getName() + "\n";
    } else {
      return "* " + i.getName() + "\n";
    }
  }
}
