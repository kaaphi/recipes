package com.kaaphi.recipe.app;

import com.google.inject.Inject;
import com.kaaphi.recipe.admin.NewUserRequest;
import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.UserShare;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class UserController implements CrudHandler {
  private UserRepository userRepo;

  @Inject
  public UserController(UserRepository userRepo) {
    this.userRepo = userRepo;
  }

  public void getAll(Context ctx) {
    ctx.json(userRepo.getAll());
  }

  @Override
  public void create(@NotNull Context context) {
    NewUserRequest user = context.bodyAsClass(NewUserRequest.class);

    userRepo.addUser(new AuthenticatableUser(user.getUser(), PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(user.getPassword())));
  }

  @Override
  public void delete(@NotNull Context context, @NotNull String s) {
    userRepo.deleteUser(new User(s));
  }

  @Override
  public void getOne(@NotNull Context context, @NotNull String s) {
    context.json(userRepo.getUserByUsername(s));
  }

  @Override
  public void update(@NotNull Context context, @NotNull String s) {
    //no op, nothing to update today
  }

  public void addShare(@NotNull Context context) {
    UserShare userShare = new UserShare(new User(context.pathParam("fromUsername")), new User(context.pathParam("toUsername")));
    userRepo.addUserShare(userShare);
    context.json(userShare);
  }

  public void getShares(@NotNull Context context) {
    String username = context.pathParam("username");
    context.json(userRepo.getSharesForUser(userRepo.getUserByUsername(username)));
  }

  public void deleteShare(@NotNull Context context) {
    userRepo.deleteUserShare(new UserShare(new User(context.pathParam("fromUsername")), new User(context.pathParam("toUsername"))));
  }

  public void changePassword(@NotNull Context context) {
    User user = userRepo.getUserByUsername(context.pathParam("id"));
    if(user == null) {
      throw new NotFoundResponse();
    }
    userRepo.setAuthDetails(user, Collections.singletonMap(PasswordAuthentication.PASSWORD_TYPE, PasswordAuthentication.generateNewDetails(context.body())));
  }
}
