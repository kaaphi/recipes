package com.kaaphi.recipe.repo.postgres;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.postgres.PostgresUserRepository.DbUser;
import com.kaaphi.recipe.users.User;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;

public class PostgresRecipeRepository extends AbstractPostgresRepository
    implements RecipeRepository {

  private final DbUser user;
  private final Gson gson;
  
  @Inject
  public PostgresRecipeRepository(DataSource ds, @Named("repoGson") Gson gson, @Assisted User user) {
    super(ds);
    this.gson = gson;
    this.user = (DbUser) user;
  }

  @Override
  public Set<RecipeBookEntry> getAll() {
    return executeQueryStream("SELECT recipe FROM getAllRecipes(?)", stmt -> stmt.setInt(1, user.getId()),
        rs -> {
          return gson.fromJson(rs.getString(1), RecipeBookEntry.class);
        })
    .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public RecipeBookEntry get(UUID id) {
    return executeQuery("SELECT recipe FROM getRecipe(?, ?)", 
        stmt -> {
          stmt.setInt(1, user.getId());
          stmt.setObject(2, id);
        },
        rs -> {
          if(rs.next()) {
            return gson.fromJson(rs.getString(1), RecipeBookEntry.class);
          } else {
            return null;
          }
        });
  }

  @Override
  public void deleteById(Set<UUID> toRemove) {
    // TODO Auto-generated method stub

  }

  @Override
  public void saveAll(Set<RecipeBookEntry> recipes) {
    recipes.forEach(this::save);
  }

  @Override
  public void save(RecipeBookEntry recipe) {
    executeCall("insertOrUpdateRecipe(?, ?, ?::JSONB)", stmt -> {
      stmt.setInt(1, user.getId());
      stmt.setObject(2, recipe.getId());
      stmt.setObject(3, gson.toJson(recipe));
    });
  }
  
  

}
