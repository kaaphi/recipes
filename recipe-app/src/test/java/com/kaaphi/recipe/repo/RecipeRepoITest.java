package com.kaaphi.recipe.repo;

import static org.junit.Assert.assertEquals;

import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.IngredientList;
import com.kaaphi.recipe.Recipe;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.app.TestRecipeModule;
import com.kaaphi.recipe.repo.RecipeRepository.RecipeCategory;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserShare;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RecipeRepoITest {
  private static RepoTestHelper helper;

  private User user1;
  private User user2;
  private RecipeRepository user1Repo;
  private RecipeRepository user2Repo;
  private UUID lastId;



  @BeforeClass
  public static void beforeClass() {
    helper = RepoTestHelper.getInstance(TestRecipeModule.createPostgresRecipeModule());
  }

  @Before
  public void before() throws Exception {
    user1 = helper.getUserRepo().addUser(helper.createAuthenticatableUser("user1")).getUser();
    user2 = helper.getUserRepo().addUser(helper.createAuthenticatableUser("user2")).getUser();

    helper.getUserRepo().addUserShare(new UserShare(user1, user2));

    user1Repo = helper.getRecipeRepo(user1);
    user2Repo = helper.getRecipeRepo(user2);
  }

  @After
  public void after() throws Exception {
    helper.rollback();
  }

  @Test
  public void addAndGetRecipe() {
    Recipe recipe = new Recipe("MyTestRecipe",
        Arrays.asList(new IngredientList(null, Arrays.asList(new Ingredient("My Ingredient")))),
        "The recipe method.",
        Arrays.asList("Source1", "Source2"));
    lastId = UUID.randomUUID();

    user1Repo.save(new RecipeBookEntry(lastId, recipe, Instant.now(), Instant.now(), user1));

    assertEquals(recipe, user1Repo.get(lastId).getRecipe());
  }

  @Test
  public void saveRecipe() {
    addAndGetRecipe();

    Recipe recipe = new Recipe("MyChangedTestRecipe",
        Arrays.asList(new IngredientList(null, Arrays.asList(new Ingredient("My Ingredient"), new Ingredient("My Other Ingredient")))),
        "The recipe method has be modified.",
        Arrays.asList("Only Source"));

    user1Repo.save(new RecipeBookEntry(lastId, recipe, Instant.now(), Instant.now(), user1));

    assertEquals(recipe, user1Repo.get(lastId).getRecipe());
  }

  @Test
  public void testShare() {
    Set<UUID> user1ids = IntStream.range(0, 5).mapToObj(i -> UUID.randomUUID()).collect(Collectors.toSet());
    Set<UUID> user2ids = IntStream.range(0, 6).mapToObj(i -> UUID.randomUUID()).collect(Collectors.toSet());
    Set<UUID> both = new HashSet<>();
    both.addAll(user1ids);
    both.addAll(user2ids);

    user1Repo.saveAll(user1ids.stream()
        .map(id -> helper.createBasicRecipeBookEntry(id, user1, id.toString()))
        .collect(Collectors.toSet())
    );
    user2Repo.saveAll(user2ids.stream()
        .map(id -> helper.createBasicRecipeBookEntry(id, user2, id.toString()))
        .collect(Collectors.toSet())
    );

    assertEquals(user1ids, asUUIDSet(user1Repo.getAll()));
    assertEquals(user1ids, asUUIDSet(user1Repo.getOwned()));
    assertEquals(Collections.emptySet(), asUUIDSet(user1Repo.getShared()));
    assertEquals(user1ids, asUUIDSet(user1Repo.getCategory(RecipeCategory.ALL)));
    assertEquals(user1ids, asUUIDSet(user1Repo.getCategory(RecipeCategory.OWNED)));
    assertEquals(Collections.emptySet(), asUUIDSet(user1Repo.getCategory(RecipeCategory.SHARED)));

    assertEquals(both, asUUIDSet(user2Repo.getAll()));
    assertEquals(user2ids, asUUIDSet(user2Repo.getOwned()));
    assertEquals(user1ids, asUUIDSet(user2Repo.getShared()));
    assertEquals(both, asUUIDSet(user2Repo.getCategory(RecipeCategory.ALL)));
    assertEquals(user2ids, asUUIDSet(user2Repo.getCategory(RecipeCategory.OWNED)));
    assertEquals(user1ids, asUUIDSet(user2Repo.getCategory(RecipeCategory.SHARED)));
  }

  private static Set<UUID> asUUIDSet(Stream<RecipeBookEntry> entries) {
    return entries
        .map(RecipeBookEntry::getId)
        .collect(Collectors.toSet());
  }

  private static Set<UUID> asUUIDSet(Collection<RecipeBookEntry> entries) {
    return asUUIDSet(entries.stream());
  }
}

