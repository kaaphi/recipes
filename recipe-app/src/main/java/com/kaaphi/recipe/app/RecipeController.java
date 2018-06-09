package com.kaaphi.recipe.app;

import static com.kaaphi.recipe.app.SessionAttributes.CURRENT_USER;
import com.github.rjeschke.txtmark.Processor;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.app.renderer.RecipeMarkdownProcessor;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.txtformat.TextFormat;
import com.kaaphi.recipe.users.RecipeRepositoryFactory;
import com.kaaphi.recipe.users.User;
import io.javalin.Context;
import io.javalin.HaltException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeController {
  private static final Logger log = LoggerFactory.getLogger(RecipeController.class);
  
  private final RecipeRepositoryFactory recipeRepoFactory;
  private final Gson gson;
  private final TextFormat txtFormat;
  
  @Inject
  public RecipeController(Gson gson, RecipeRepositoryFactory recipeRepoFactory, TextFormat txtFormat) {
    this.recipeRepoFactory = recipeRepoFactory;
    this.gson = gson;
    this.txtFormat = txtFormat;
  }
  
  public void renderRecipeList(Context ctx) {
	  ctx.renderVelocity("/index.html", getRecipeListModel(ctx));
  }
  
  public void renderRecipe(Context ctx) {
	  ctx.renderVelocity("/recipe.html", getRecipeModel(ctx));
  }
  
  public void renderEditRecipe(Context ctx) {
	  ctx.renderVelocity("/recipe_edit.html", getRecipeEditModel(ctx));
  }
  
  public void renderDeleteRecipe(Context ctx) {
    ctx.renderVelocity("/recipe_delete.html", getRecipeEditModel(ctx));
  }
  
  public void renderNewRecipe(Context ctx) {
    ctx.renderVelocity("/recipe_create.html", new HashMap<>());
  }
  
  public Map<String, Object> getRecipeListModel(Context ctx) {
    return model(b -> b.put("allRecipes", recipeRepo(ctx).getAll()));
  }
  
  public Map<String, Object> getRecipeEditModel(Context ctx) {
    RecipeBookEntry r = recipeRepo(ctx).get(parseUUID(ctx));
    
    return model(b -> b
        .put("recipe", r)
        .put("recipeTxt", txtFormat.toTextString(r.getRecipe()))     
        );
  }
  
  public Map<String, Object> getRecipeModel(Context ctx) {
    RecipeBookEntry r = recipeRepo(ctx).get(parseUUID(ctx));
    
    List<String> ingredients = r.getRecipe().getIngredients().stream()
        .map(i -> i.getQuantity().isPresent() ? String.format("%s %s", i.getQuantity().get(), i.getName()) : i.getName())
        .collect(Collectors.toList());
    
    return model(b -> b
        .put("recipe", r)
        .put("ingredients", ingredients)
        .put("method", Processor.process(r.getRecipe().getMethod()))
        );
  }
  
  private static Map<String, Object> model(Consumer<ImmutableMap.Builder<String, Object>> consumer) {
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    consumer.accept(builder);
    return new HashMap<>(builder.build());
  }
  
  public void readAllRecipes(Context ctx) {
    Set<RecipeBookEntry> recipes = recipeRepo(ctx).getAll();
    
    StringBuilder sb = new StringBuilder();
    gson.toJson(recipes, new TypeToken<Collection<RecipeBookEntry>>(){}.getType(), sb);
    
    ctx.result(sb.toString());    
  }
  
  public void readRecipe(Context ctx) {
    UUID uuid = parseUUID(ctx);
    
    RecipeBookEntry recipe = recipeRepo(ctx).get(uuid);
    
    if(recipe != null) {
      ctx.result(gson.toJson(recipe));
    } else {
      ctx.status(404);
    }   
  }
  
  public void updateRecipe(Context ctx) {
    UUID id = parseUUID(ctx);
    
    Recipe recipe = parseRecipe(ctx);
        
    recipeRepo(ctx).save(id, recipe);
  }
  
  private Recipe parseRecipe(Context ctx) {
      switch(ctx.contentType()) {
        case "text/plain" : return txtFormat.fromText(ctx.body());
        case "application/json" : return gson.fromJson(ctx.body(), Recipe.class);
        default: throw new HaltException(400);
      }
  }
  
  public void createRecipe(Context ctx) {
    Recipe recipe = parseRecipe(ctx);
    
    RecipeBookEntry entry = recipeRepo(ctx).save(UUID.randomUUID(), recipe);
    
    ctx.result(gson.toJson(entry));
  }
  
  public void deleteRecipe(Context ctx) {
    UUID uuid = parseUUID(ctx);
    recipeRepo(ctx).delete(uuid);
  } 
  
  public void render(Context ctx) {
    UUID uuid = parseUUID(ctx);
    RecipeBookEntry recipe = recipeRepo(ctx).get(uuid);
    ctx.result(RecipeMarkdownProcessor.process(recipe.getRecipe()));
  }
  
  private RecipeRepository recipeRepo(Context ctx) {
    return recipeRepoFactory.createRepository(getUser(ctx));
  }
  
  private static User getUser(Context ctx) {
    User user = ctx.sessionAttribute(CURRENT_USER);
    if(user == null) {
      throw new HaltException(401);
    } else {
      return user;
    }
  }
  
  private static UUID parseUUID(Context ctx) {
    return parseUUID(ctx.param("id"));
  }
  
  private static UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (NumberFormatException e) {
      log.debug("No recipe for id <{}>", id);
      throw new HaltException(404, "Not found");
    }
  }
}
