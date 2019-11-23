package com.kaaphi.recipe.repo.postgres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.repo.postgres.PostgresUserRepository.DbUser;
import com.kaaphi.recipe.users.User;

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
    return executeQueryStream("SELECT id, recipe, createdTime, updatedTime FROM getAllRecipes(?)", stmt -> stmt.setInt(1, user.getId()), this::getRecipeBookEntryFromResultSet)
    .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Override
  public RecipeBookEntry get(UUID id) {
    return executeQuery("SELECT id, recipe, createdTime, updatedTime FROM getRecipe(?, ?)", 
        stmt -> {
          stmt.setInt(1, user.getId());
          stmt.setObject(2, id);
        },
        rs -> {
          if(rs.next()) {
            return getRecipeBookEntryFromResultSet(rs);
          } else {
            return null;
          }
        });
  }
  
  private RecipeBookEntry getRecipeBookEntryFromResultSet(ResultSet rs) throws JsonSyntaxException, SQLException {
    return new RecipeBookEntry((UUID)rs.getObject(1), gson.fromJson(rs.getString(2), Recipe.class), toInstant(rs.getTimestamp(3)), toInstant(rs.getTimestamp(4)));
  }
  
  private static Instant toInstant(Timestamp ts) {
    return Optional.ofNullable(ts).map(Timestamp::toInstant).orElse(null);
  }

  @Override
  public void deleteById(Set<UUID> toRemove) {
    executeCall("deleteRecipes(?, ?)", stmt -> {
      stmt.setInt(1, user.getId());
      stmt.setArray(2,stmt.getConnection().createArrayOf("UUID", toRemove.toArray()) );
    });
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
      stmt.setObject(3, gson.toJson(recipe.getRecipe()));
    });
  }
  
  

}
