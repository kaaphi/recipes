package com.kaaphi.recipe.users;

public class User {
  private final String username;

  public User(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
  
  public String toString() {
    return String.format("User[%s]", username);
  }
}
