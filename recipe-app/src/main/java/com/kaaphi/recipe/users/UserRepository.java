package com.kaaphi.recipe.users;

public interface UserRepository {
  public User getUserByUsername(String username);
  public User addUser(User user);
  public void updateUser(User user);
  public void deleteUser(User user);
}
