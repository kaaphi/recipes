package com.kaaphi.recipe.txtformat;

import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.Recipe;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextFormat {
  private static final String NEWLINE = "\r\n";
  
  public Recipe fromText(Reader reader) {
    IngredientParser ingredientParser = new IngredientParser();
    
    BufferedReader in = new BufferedReader(reader);
    Iterator<String> it = in.lines().iterator();
    String title = it.next();
    
    List<Ingredient> ingredients = nextChunk(it)
    .map(ingredientParser::fromString)
    .collect(Collectors.toList());
    
    String method = nextChunk(it).collect(Collectors.joining(NEWLINE));
    
    return new Recipe(title, ingredients, method);
  }
  
  public void toText(Recipe recipe, Appendable out) throws IOException {
    out.append(recipe.getTitle());
    out.append(NEWLINE);
    for(Ingredient i : recipe.getIngredients()) {
      if(i.getQuantity().isPresent()) {
        out.append(i.getQuantity().get()).append(" ");
      }
      out.append(i.getName());
      out.append(NEWLINE);
    }
    out.append(recipe.getMethod());
    out.append(NEWLINE);
  }
  
  private static Stream<String> nextChunk(Iterator<String> it) {
    String line = null;
    while(it.hasNext() && (line = it.next()).isEmpty());
    Stream.Builder<String> builder = Stream.builder();
    if(line != null) {

      builder.add(line);
      while(it.hasNext() && !(line = it.next()).isEmpty()) {
        builder.add(line);
      }
    }
    return builder.build();
  }
}
