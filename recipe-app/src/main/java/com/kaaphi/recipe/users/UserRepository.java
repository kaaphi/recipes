package com.kaaphi.recipe.users;

import java.util.List;
import java.util.Map;

public interface UserRepository {
  public List<User> getAll();
  public User getUserByUsername(String username);
  public AuthenticatableUser addUser(AuthenticatableUser user);
  public void updateUser(User user);
  public void deleteUser(User user);
  public AuthenticatableUser getAuthenticatableUser(String username);
  public void setAuthDetails(User user, Map<String,String> details);
  public List<UserShare> getSharesForUser(User user);
  public void addUserShare(UserShare share);  
  public void deleteUserShare(UserShare share);
}
