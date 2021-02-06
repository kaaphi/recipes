package com.kaaphi.recipe.app;

import static com.kaaphi.recipe.app.SessionAttributes.getUser;

import com.google.inject.Inject;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.repo.RecipeRepository;
import com.kaaphi.recipe.users.RecipeRepositoryFactory;
import io.javalin.http.Context;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeaheadSearchController {
  private static final Logger log = LoggerFactory.getLogger(RecipeController.class);

  private final RecipeRepositoryFactory recipeRepoFactory;

  @Inject
  public TypeaheadSearchController(RecipeRepositoryFactory recipeRepoFactory) {
    this.recipeRepoFactory = recipeRepoFactory;
  }

  public void readRecipeTitles(Context ctx) {
    ctx.json(getTitles(recipeRepoFactory.createRepository(getUser(ctx)), ctx.queryParam("q")));
  }

  static List<String> getTitles(RecipeRepository repo, @Nullable String queryString) {
    Function<String, Optional<RecipeTitleResult>> query = queryString == null || queryString.isEmpty() ?
        title -> Optional.of(new RecipeTitleResult(RecipeTitleResultType.STARTS_WITH, title)) :
        new RecipeTitleQuery(queryString);

    return repo.getAll().stream()
        .map(RecipeBookEntry::getRecipe)
        .map(Recipe::getTitle)
        .map(query)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .sorted()
        .map(RecipeTitleResult::getTitle)
        .collect(Collectors.toList());
  }

  private static Predicate<String> makeQueryPredicate(String query) {
    final String finalQuery = query.toLowerCase();
    return str -> str.toLowerCase().contains(finalQuery);
  }

  static class RecipeTitleQuery implements Function<String, Optional<RecipeTitleResult>> {
    private final String queryString;

    public RecipeTitleQuery(String queryString) {
      this.queryString = queryString.toLowerCase();
    }

    public Optional<RecipeTitleResult> apply(String title) {
      int i = title.toLowerCase().indexOf(queryString);
      if(i < 0) {
        return Optional.empty();
      } else {
        RecipeTitleResultType resultType;
        if(i == 0) {
          resultType = RecipeTitleResultType.STARTS_WITH;
        } else if (Character.isWhitespace(title.charAt(i-1))) {
          resultType = RecipeTitleResultType.TOKEN_STARTS_WITH;
        } else {
          resultType = RecipeTitleResultType.CONTAINS;
        }

        return Optional.of(new RecipeTitleResult(resultType, title));
      }
    }
  }


  enum RecipeTitleResultType {
    STARTS_WITH,
    TOKEN_STARTS_WITH,
    CONTAINS
  }
  static class RecipeTitleResult implements Comparable<RecipeTitleResult> {
    private final RecipeTitleResultType resultType;
    private final String title;

    public RecipeTitleResult(
        RecipeTitleResultType resultType, String title) {
      this.resultType = resultType;
      this.title = title;
    }

    public String getTitle() {
      return title;
    }

    public RecipeTitleResultType getResultType() {
      return resultType;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      RecipeTitleResult that = (RecipeTitleResult) o;
      return resultType == that.resultType && title.equals(that.title);
    }

    @Override
    public int hashCode() {
      return Objects.hash(resultType, title);
    }

    @Override
    public int compareTo(@NotNull RecipeTitleResult that) {
      return this.resultType == that.resultType ?
          this.title.compareTo(that.title) :
          this.resultType.compareTo(that.resultType);
    }
  }
}
