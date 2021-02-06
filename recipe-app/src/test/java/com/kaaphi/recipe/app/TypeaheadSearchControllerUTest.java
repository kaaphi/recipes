package com.kaaphi.recipe.app;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.repo.RecipeRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class TypeaheadSearchControllerUTest {
  private TypeaheadSearchController controller;

  private RecipeRepository repo = mock(RecipeRepository.class);

  @Test
  public void test() {
    List<String> allRecipeTitles = Arrays.asList("A Recipe", "recipe b", "Apple Pie", "Pineapple", "Pie with Apples", "Crabapple PieRecipe");
    List<String> sortedRecipeTitles = new ArrayList<>(allRecipeTitles);
    Collections.sort(sortedRecipeTitles);

    setUpRepo(allRecipeTitles.stream());


    assertEquals(Arrays.asList("Apple Pie", "Pie with Apples", "Crabapple PieRecipe", "Pineapple"), TypeaheadSearchController.getTitles(repo, "app"));
    assertEquals(Arrays.asList("recipe b", "A Recipe", "Crabapple PieRecipe"), TypeaheadSearchController.getTitles(repo, "recipe"));
    assertEquals(Collections.emptyList(), TypeaheadSearchController.getTitles(repo, "blob"));
    assertEquals(sortedRecipeTitles, TypeaheadSearchController.getTitles(repo, ""));
    assertEquals(sortedRecipeTitles, TypeaheadSearchController.getTitles(repo, null));
  }

  private void setUpRepo(Stream<String> recipeTitles) {
    when(repo.getAll()).thenReturn(recipeTitles
    .map(title -> new RecipeBookEntry(UUID.randomUUID(), new Recipe(title, Collections.emptyList(), "", Collections.emptyList()), Instant
        .now(), Instant.now(), null)).collect(Collectors.toSet()));
  }
}
