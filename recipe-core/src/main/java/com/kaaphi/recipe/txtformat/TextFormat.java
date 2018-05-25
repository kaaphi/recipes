package com.kaaphi.recipe.txtformat;

import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.Recipe;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
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
    
    String method = nextChunks(it, line -> "SOURCES".equals(line)).collect(Collectors.joining(NEWLINE));
    
    List<String> sources = nextChunk(it)
        .collect(Collectors.toList());
        
    return new Recipe(title, ingredients, method, sources);
  }
  
  public Recipe fromText(String text) {
    try(Reader r = new StringReader(text)) {
      return fromText(r);
    } catch (IOException e) {
      throw new Error(e); //this really shouldn't happen with StringBuilder 
    }
  }
  
  public void toText(Recipe recipe, Appendable out) throws IOException {
    out.append(recipe.getTitle());
    out.append(NEWLINE).append(NEWLINE);
    for(Ingredient i : recipe.getIngredients()) {
      if(i.getQuantity().isPresent()) {
        out.append(i.getQuantity().get()).append(" ");
      }
      out.append(i.getName());
      out.append(NEWLINE);
    }
    out.append(NEWLINE)
    .append(recipe.getMethod())
    .append(NEWLINE).append(NEWLINE);
    
    if(!recipe.getSources().isEmpty()) {
      out.append("SOURCES").append(NEWLINE);
      for(String source : recipe.getSources()) {
        out.append(source).append(NEWLINE);
      }
    }
  }
  
  public String toTextString(Recipe recipe) {
    StringBuilder sb = new StringBuilder();
    try {
      toText(recipe, sb);
    } catch (IOException e) {
      throw new Error(e); //this really shouldn't happen with StringBuilder
    }
    return sb.toString();
  }
  
  private static Stream<String> nextChunk(Iterator<String> it) {
    return nextChunks(it, String::isEmpty);
  }
  
  
  private static Stream<String> nextChunks(Iterator<String> it, Predicate<String> until) {
    String line = null;
    while(it.hasNext() && (line = it.next()).isEmpty());
    Stream.Builder<String> builder = Stream.builder();
    if(line != null) {

      builder.add(line);
      while(it.hasNext() && !(until.test(line = it.next()))) {
        builder.add(line);  
      }
    }
    return builder.build();
  }
}
