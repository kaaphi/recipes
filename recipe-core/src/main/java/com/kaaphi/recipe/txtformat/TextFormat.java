package com.kaaphi.recipe.txtformat;

import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.Recipe;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextFormat {
  public Recipe fromText(UUID id, Reader reader) {
    IngredientParser ingredientParser = new IngredientParser();
    
    BufferedReader in = new BufferedReader(reader);
    Iterator<String> it = in.lines().iterator();
    String title = it.next();
    
    List<Ingredient> ingredients = nextChunk(it)
    .map(ingredientParser::fromString)
    .collect(Collectors.toList());
    
    String method = nextChunk(it).collect(Collectors.joining("\r\n"));
    
    return new Recipe(id, title, ingredients, method);
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
