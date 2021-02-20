package com.kaaphi.recipe.app;

import static com.kaaphi.recipe.app.SessionAttributes.getUser;

import com.github.rjeschke.txtmark.Processor;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.kaaphi.recipe.IngredientList;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.app.renderer.RecipeMarkdownProcessor;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.RecipeRepository.RecipeCategory;
import com.kaaphi.recipe.repo.RecipeSearchResult;
import com.kaaphi.recipe.txtformat.TextFormat;
import com.kaaphi.recipe.users.RecipeRepositoryFactory;
import com.kaaphi.recipe.users.User;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
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
  
  public void renderAllRecipeList(Context ctx) {
	  ctx.render("/recipeList.html", getRecipeListModel(ctx, "All Recipes", RecipeRepository::getAll));
  }
  
  public void renderOwnedRecipeList(Context ctx) {
    ctx.render("/recipeList.html", getRecipeListModel(ctx, "My Recipes", RecipeRepository::getOwned));
  }
  
  public void renderSharedRecipeList(Context ctx) {
    ctx.render("/recipeList.html", getRecipeListModel(ctx, "Shared Recipes", RecipeRepository::getShared));
  }
  
  public void renderRecipeSearch(Context ctx) {
    String searchString = ctx.queryParam("q");
    RecipeCategory scope = RecipeCategory.optionalValueOf(ctx.queryParam("scope")).orElse(RecipeCategory.OWNED);
    boolean includeArchive = Optional.ofNullable(ctx.queryParam("includeArchive")).map(Boolean::valueOf).orElse(false);
    
    if(searchString != null) {
      Set<RecipeSearchResult> results = recipeRepo(ctx).searchRecipes(scope, includeArchive, searchString);

      if(results.size() == 1 && results.iterator().next().isTitleMatch()) {
        //if we only matched one thing and it was by title, just redirect to the recipe instead
        ctx.redirect("/recipe/" + results.iterator().next().getEntry().getId());
      } else {
        ctx.render("/recipeSearchResult.html", model(b -> b
            .put("title", String.format("%s - Recipes", searchString))
            .put("searchString", searchString)
            .put("scope", scope.name())
            .put("results", results)
        ));
      }
    } else {
      ctx.render("/recipeSearchResult.html", model(b -> b
          .put("title", "Recipes")
          .put("results", Collections.emptySet())
          ));
    }
  }
  
  public void renderRecipe(Context ctx) {
	  ctx.render("/recipe.html", getRecipeModel(ctx));
  }
  
  public void renderEditRecipe(Context ctx) {
	  ctx.render("/recipe_edit.html", getRecipeEditModel(ctx));
  }
  
  public void renderDeleteRecipe(Context ctx) {
    ctx.render("/recipe_delete.html", getRecipeEditModel(ctx));
  }
  
  public void renderNewRecipe(Context ctx) {
    ctx.render("/recipe_create.html", new HashMap<>());
  }
  
  public Map<String, Object> getRecipeListModel(Context ctx, String title, Function<RecipeRepository, Set<RecipeBookEntry>> getRecipes) {
    //build a map of starting character to list of recipes with a title starting with that character
    Map<String, Set<RecipeBookEntry>> entries = getRecipes.apply(recipeRepo(ctx)).stream()
        .collect(Collectors.toMap(
            r -> r.getRecipe().getTitle().toUpperCase().substring(0,1),
            r -> new LinkedHashSet<>(Collections.singleton(r)),
            (a,b) -> {a.addAll(b); return a;},
            LinkedHashMap::new));

    return model(b -> b
        .put("title", title)
        .put("allRecipes", entries)
        );
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
    
    List<IngredientListModel> ingredientLists = r.getRecipe().getIngredientLists().stream()
        .map(IngredientListModel::new)
        .collect(Collectors.toList());
    
    return model(b -> b
        .put("recipe", r)
        .put("owner", r.getOwner().getUsername())
        .put("ownedByCurrentUser", r.getOwner().equals(getUser(ctx)))
        .put("ingredientLists", ingredientLists)
        .put("method", Processor.process(r.getRecipe().getMethod()))
        );
  }
  
  public static class IngredientListModel {
    private final String name;
    private final List<String> ingredients;
    
    public IngredientListModel(IngredientList list) {
      name = list.getName().orElse(null);
      ingredients = list.getIngredients().stream()
          .map(i -> i.getQuantity().map(q -> String.format("%s %s", q, i.getName())).orElse(i.getName()))
          .collect(Collectors.toList());
    }

    public String getName() {
      return name;
    }

    public List<String> getIngredients() {
      return ingredients;
    }
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
    
    RecipeRepository repo = recipeRepo(ctx);
    User user = getUser(ctx);
    
    RecipeBookEntry current = repo.get(id);
    if(current == null) {
      ctx.status(404);
    } else if(!current.getOwner().equals(user)) {
      ctx.status(401);
    } else {
      RecipeBookEntry entry = new RecipeBookEntry(id, recipe, current.getCreated(), Instant.now(), user, current.isArchived());
      repo.save(entry);
    }
  }
  
  private Recipe parseRecipe(Context ctx) {
      switch(ctx.contentType()) {
        case "text/plain" : return txtFormat.fromText(ctx.body());
        case "application/json" : return gson.fromJson(ctx.body(), Recipe.class);
        default: throw new BadRequestResponse();
      }
  }
  
  public void createRecipe(Context ctx) {
    Recipe recipe = parseRecipe(ctx);
    
    RecipeBookEntry entry = new RecipeBookEntry(UUID.randomUUID(), recipe, Instant.now(), null, getUser(ctx), false);
    
    recipeRepo(ctx).save(entry);
    
    ctx.result(gson.toJson(entry));
  }
  
  public void deleteRecipe(Context ctx) {
    UUID uuid = parseUUID(ctx);
    boolean isArchive = Optional.ofNullable(ctx.queryParam("archive")).map(Boolean::valueOf).orElse(false);
    
    RecipeRepository repo = recipeRepo(ctx);
    User user = getUser(ctx);
    
    RecipeBookEntry current = repo.get(uuid);
    if(current == null) {
      ctx.status(404);
    } else if(!current.getOwner().equals(user)) {
      ctx.status(401);
    } else {
      if(isArchive) {
        repo.archiveById(Collections.singleton(uuid));
      } else {
        repo.delete(uuid);
      }
    }
  } 
  
  public void render(Context ctx) {
    UUID uuid = parseUUID(ctx);
    RecipeBookEntry recipe = recipeRepo(ctx).get(uuid);
    ctx.result(RecipeMarkdownProcessor.process(recipe.getRecipe()));
  }
  
  private RecipeRepository recipeRepo(Context ctx) {
    return recipeRepoFactory.createRepository(getUser(ctx));
  }
  
  private static UUID parseUUID(Context ctx) {
    return parseUUID(ctx.pathParam("id"));
  }
  
  private static UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (NumberFormatException e) {
      log.debug("No recipe for id <{}>", id);
      throw new NotFoundResponse();
    }
  }
}
