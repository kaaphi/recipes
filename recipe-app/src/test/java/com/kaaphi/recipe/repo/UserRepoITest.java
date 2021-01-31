package com.kaaphi.recipe.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.kaaphi.recipe.app.TestRecipeModule;
import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.UserRole;
import com.kaaphi.recipe.users.UserShare;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserRepoITest {
  private static RepoTestHelper helper;
  private UserRepository repo = helper.getUserRepo();

  @BeforeClass
  public static void beforeClass() {
    helper = RepoTestHelper.getInstance(TestRecipeModule.createPostgresRecipeModule());
  }

  @After
  public void after() throws Exception {
    helper.rollback();
  }

  @Test
  public void addUser() {
    AuthenticatableUser addedUser = repo.addUser(helper.createAuthenticatableUser("myTestUser"));
    assertNotNull(addedUser);

    assertEquals(new User("myTestUser"), repo.getUserByUsername("myTestUser"));

    assertEquals(EnumSet.of(UserRole.USER), repo.getRolesForUser(addedUser.getUser()));
  }

  @Test
  public void addUserShare() {
    AuthenticatableUser user1 = repo.addUser(helper.createAuthenticatableUser("user1"));
    AuthenticatableUser user2 = repo.addUser(helper.createAuthenticatableUser("user2"));

    repo.addUserShare(new UserShare(user1.getUser(), user2.getUser()));

    assertEquals(Arrays.asList(new UserShare(user1.getUser(), user2.getUser())), repo.getSharesForUser(user1.getUser()));
    assertEquals(Collections.emptyList(), repo.getSharesForUser(user2.getUser()));
  }

  @Test
  public void getAll() {
    AuthenticatableUser user1 = repo.addUser(helper.createAuthenticatableUser("user1"));
    AuthenticatableUser user2 = repo.addUser(helper.createAuthenticatableUser("user2"));

    assertEquals(new HashSet<>(Arrays.asList(new User("user1"), new User("user2"))), new HashSet<>(repo.getAll()));
  }

  @Test
  public void removeUserShare() {
    addUserShare();

    repo.deleteUserShare(new UserShare(new User("user1"), new User("user2")));

    assertEquals(Collections.emptyList(), repo.getSharesForUser(new User("user1")));
  }

  @Test
  public void removeUserNoRecipes() {
    addUser();

    repo.deleteUser(new User("myTestUser"));

    assertNull(repo.getUserByUsername("myTestUser"));
  }

  @Test
  public void removeUserWithShare() {
    addUserShare();

    repo.deleteUser(new User("user1"));

    assertNull(repo.getUserByUsername("user1"));
  }

  @Test
  public void removeUserWithRecipes() {
    addUser();

    User user = repo.getUserByUsername("myTestUser");

    helper.getRecipeRepo(user).save(helper.createBasicRecipeBookEntry(user, "myTestRecipe"));

    repo.deleteUser(new User("myTestUser"));

    assertNull(repo.getUserByUsername("myTestUser"));
  }
}
