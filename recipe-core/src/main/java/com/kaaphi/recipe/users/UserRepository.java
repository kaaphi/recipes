package com.kaaphi.recipe.users;

public interface UserRepository {
  public User getUserByUsername(String username);
  public void addUser(User user);
  public void updateUser(User user);
  public void deleteUser(User user);
}
