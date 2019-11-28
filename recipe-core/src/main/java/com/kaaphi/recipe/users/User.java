package com.kaaphi.recipe.users;

import java.util.Objects;

public class User {
  private final String username;

  public User(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof User)) {
      return false;
    }
    User other = (User) obj;
    return Objects.equals(username, other.username);
  }

  public String toString() {
    return String.format("User[%s]", username);
  }
}
