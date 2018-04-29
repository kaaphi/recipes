package com.kaaphi.recipe.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.app.renderer.RecipeMarkdownProcessor;
import com.kaaphi.recipe.repo.RecipeRepository;
import io.javalin.Context;
import io.javalin.HaltException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class RecipeController {
  
  private final RecipeRepository repo;
  private final Gson gson;
  
  @Inject
  public RecipeController(Gson gson, RecipeRepository repo) {
    this.repo = repo;
    this.gson = gson;
  }
  
  public void readAllRecipes(Context ctx) {
    Set<Recipe> recipes = repo.getAll();
    
    StringBuilder sb = new StringBuilder();
    gson.toJson(recipes, new TypeToken<Collection<Recipe>>(){}.getType(), sb);
    
    ctx.result(sb.toString());    
  }
  
  public void readRecipe(Context ctx) {
    UUID uuid = parseUUID(ctx);
    
    Recipe recipe = repo.get(uuid);
    
    if(recipe != null) {
      ctx.result(gson.toJson(recipe));
    } else {
      ctx.status(404);
    }   
  }
  
  public void updateRecipe(Context ctx) {
    UUID id = parseUUID(ctx);
    Recipe recipe = gson.fromJson(ctx.body(), Recipe.class);
    
    if(recipe.getId() != null && !recipe.getId().equals(id)) {
      throw new IllegalArgumentException("IDs do not match!");
    }
    
    repo.save(recipe);
  }
  
  public void createRecipe(Context ctx) {
    Recipe recipe = gson.fromJson(ctx.body(), Recipe.class);
    
    recipe = recipe.withId(UUID.randomUUID());
    
    repo.save(recipe);
    
    ctx.result(gson.toJson(recipe));
  }
  
  public void deleteRecipe(Context ctx) {
    UUID uuid = parseUUID(ctx);
    repo.delete(uuid);
  }
  
  public void render(Context ctx) {
    UUID uuid = parseUUID(ctx);
    Recipe recipe = repo.get(uuid);
    ctx.result(RecipeMarkdownProcessor.process(recipe));
  }
  
  private static UUID parseUUID(Context ctx) {
    return parseUUID(ctx.param("id"));
  }
  
  private static UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (NumberFormatException e) {
      throw new HaltException(404, "Not found");
    }
  }
}
