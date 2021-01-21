package com.kaaphi.recipe.app.admin;

import com.kaaphi.recipe.users.User;

public class NewUserRequest {
  private User user;
  private String password;

  public NewUserRequest() {
  }

  public NewUserRequest(User user, String password) {
    this.user = user;
    this.password = password;
  }

  public User getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}
