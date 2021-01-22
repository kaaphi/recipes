package com.kaaphi.recipe.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.inject.Inject;
import com.kaaphi.console.CommandContextClass;
import com.kaaphi.console.Confidential;
import com.kaaphi.console.ConsoleCommand;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserShare;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;

@CommandContextClass("user")
public class UserAdmin {
  
  private RecipeApiClient recipeApiClient;
  
  @Inject
  public UserAdmin(RecipeApiClient recipeApiClient) {
    this.recipeApiClient = recipeApiClient;
  }
  
  @ConsoleCommand
  public void add(PrintWriter out, String username, @Confidential String password) throws IOException {
    recipeApiClient.post("/api/users", new NewUserRequest(new User(username), password), null);
  }
  
  @ConsoleCommand
  public void setPassword(PrintWriter out, String username, @Confidential String password) throws IOException {
    try {
      recipeApiClient.put("/api/users/" + username + "/password", new StringEntity(password), null);
    } catch (HttpResponseException e) {
      if(e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
        out.println("No user with that name exists!");
      } else {
        throw e;
      }
    }
  }
  
  @ConsoleCommand
  public void delete(PrintWriter out, String username) throws IOException {
    recipeApiClient.delete("/api/users/" + username);
  }
  
  @ConsoleCommand
  public void showShare(PrintWriter out, String username) throws IOException {
    recipeApiClient.get("/api/usershare/" + username, new TypeReference<List<UserShare>>() {})
    .stream()
    .map(UserShare::getToUser)
    .map(User::getUsername)
    .forEach(out::println);
  }
  
  @ConsoleCommand
  public void addShare(PrintWriter out, String fromUserName, String toUserName) throws IOException {
    recipeApiClient.post(String.format("/api/usershare/%s/%s", fromUserName, toUserName), null, null);
  }
  
  @ConsoleCommand
  public void deleteShare(PrintWriter out, String fromUserName, String toUserName) throws IOException {
    recipeApiClient.delete(String.format("/api/usershare/%s/%s", fromUserName, toUserName));
  }
  
  @ConsoleCommand
  public void showAll(PrintWriter out) throws IOException {
    recipeApiClient.get("/api/users", new TypeReference<List<User>>() {})
        .stream()
        .map(User::getUsername)
        .forEach(out::println);
  }
  
  @ConsoleCommand
  public void show(PrintWriter out, String username) throws IOException {
    User user = recipeApiClient.get("/api/users/" + username, new TypeReference<User>() {});
    out.println(user);
  }
}
