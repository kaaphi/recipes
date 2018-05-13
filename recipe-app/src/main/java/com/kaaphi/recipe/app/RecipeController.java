package com.kaaphi.recipe.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.app.renderer.RecipeMarkdownProcessor;
import com.kaaphi.recipe.repo.RecipeRepository;
import io.javalin.Context;
import io.javalin.HaltException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeController {
  private static final Logger log = LoggerFactory.getLogger(RecipeController.class);
  
  private final RecipeRepository repo;
  private final Gson gson;
  
  @Inject
  public RecipeController(Gson gson, RecipeRepository repo) {
    this.repo = repo;
    this.gson = gson;
  }
  
  public Map<String, Object> getRecipeListModel(Context ctx) {
    return new HashMap<>(Collections.singletonMap("allRecipes", repo.getAll()));
  }
  
  public Map<String, Object> getRecipeModel(Context ctx) {
    return Collections.singletonMap("recipe", RecipeMarkdownProcessor.process(repo.get(parseUUID(ctx)).getRecipe()));
  }
  
  public void readAllRecipes(Context ctx) {
    Set<RecipeBookEntry> recipes = repo.getAll();
    
    StringBuilder sb = new StringBuilder();
    gson.toJson(recipes, new TypeToken<Collection<RecipeBookEntry>>(){}.getType(), sb);
    
    ctx.result(sb.toString());    
  }
  
  public void readRecipe(Context ctx) {
    UUID uuid = parseUUID(ctx);
    
    RecipeBookEntry recipe = repo.get(uuid);
    
    if(recipe != null) {
      ctx.result(gson.toJson(recipe));
    } else {
      ctx.status(404);
    }   
  }
  
  public void updateRecipe(Context ctx) {
    UUID id = parseUUID(ctx);
    Recipe recipe = gson.fromJson(ctx.body(), Recipe.class);
        
    repo.save(id, recipe);
  }
  
  public void createRecipe(Context ctx) {
    Recipe recipe = gson.fromJson(ctx.body(), Recipe.class);
    
    repo.save(UUID.randomUUID(), recipe);
    
    ctx.result(gson.toJson(recipe));
  }
  
  public void deleteRecipe(Context ctx) {
    UUID uuid = parseUUID(ctx);
    repo.delete(uuid);
  }
  
  public void render(Context ctx) {
    UUID uuid = parseUUID(ctx);
    RecipeBookEntry recipe = repo.get(uuid);
    ctx.result(RecipeMarkdownProcessor.process(recipe.getRecipe()));
  }
  
  private static UUID parseUUID(Context ctx) {
    return parseUUID(ctx.param("id"));
  }
  
  private static UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (NumberFormatException e) {
      log.debug("No recipe for id <%s>", id);
      throw new HaltException(404, "Not found");
    }
  }
}
