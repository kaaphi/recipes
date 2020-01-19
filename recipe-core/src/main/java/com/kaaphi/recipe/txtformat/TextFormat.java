package com.kaaphi.recipe.txtformat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.IngredientList;
import com.kaaphi.recipe.Recipe;

public class TextFormat {
  private static final String NEWLINE = "\r\n";
  private static final IngredientParser ingredientParser = new IngredientParser();
  
  public Recipe fromText(Reader reader) {
    
    BufferedReader in = new BufferedReader(reader);
    
    TextIterator it = new TextIterator(in.lines().iterator());
    String title = it.next();
    
    List<IngredientList> ingredientLists = new LinkedList<>();
    
    //always at least one ingredient list
    IngredientList list;
    boolean isDefault = true;
    while((list = parseIngredientList(it, isDefault)) != null) {
      isDefault = false;
      ingredientLists.add(list);
    }
    
    String method = it.nextChunk(line -> "SOURCES".equals(line)).collect(Collectors.joining(NEWLINE));

    //skip the SOURCES tag line
    if(it.hasNext()) {
      it.next();
    }
    List<String> sources = it.nextChunk(String::isEmpty)
        .collect(Collectors.toList());
        
    return new Recipe(title, ingredientLists, method, sources);
  }
  
  private IngredientList parseIngredientList(TextIterator it, boolean isDefault) {
    String title;

    it.skipEmptyLines();
    if(!it.hasNext()) {
      return null;
    }

    String firstLine = it.next();

    //Titled ingredient list
    if(firstLine.endsWith(":")) {
      title = firstLine;
    } else {
      it.undoRead();
      if(isDefault) {
        title = null;
      } else {
        //no more ingredient lists
        return null;
      }
    }
    
    List<Ingredient> ingredients = it.nextChunk(String::isEmpty)
        .map(ingredientParser::fromString)
        .collect(Collectors.toList());
    
    return new IngredientList(title, ingredients);
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
    out.append(NEWLINE);
    for(IngredientList il : recipe.getIngredientLists()) {
      out.append(NEWLINE);
      if(il.getName().isPresent()) {
        out.append(il.getName().get());
        out.append(NEWLINE);
      }
      for(Ingredient i : il.getIngredients()) {
        if(i.getQuantity().isPresent()) {
          out.append(i.getQuantity().get()).append(" ");
        }
        out.append(i.getName());
        out.append(NEWLINE);
      }
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
}
