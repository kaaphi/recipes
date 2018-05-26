package com.kaaphi.recipe.repo.jsonfile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.kaaphi.recipe.RecipeBookEntry;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserFileRepository extends AbstractFileRepo implements UserRepository {

  private final Map<String, User> cache;
  private final Gson gson;
  
  @Inject
  public UserFileRepository(@Named("jsonRepoPath") String storeDirectory,  @Named("repoGson") Gson gson) {
    super(storeDirectory, "users.json");
    this.gson = gson;
    cache = new ConcurrentHashMap<>(readAll());
  }

  private Map<String, User> readAll() {
    return read(in -> {
      if(in.isPresent()) {
        List<User> users =  gson.fromJson(in.get(), new TypeToken<LinkedList<User>>(){}.getType());
        return users.stream().collect(Collectors.toMap(User::getUsername, Function.identity()));
      } else {
        return Collections.emptyMap();
      }
    });
  }
  
  private void write() {
    write(out -> gson.toJson(new LinkedList<>(cache.values()), new TypeToken<Collection<RecipeBookEntry>>(){}.getType(), out));    
  }
  
  @Override
  public User getUserByUsername(String username) {
    return cache.get(username);
  }

  @Override
  public void addUser(User user) {
    if(cache.containsKey(user.getUsername())) {
      throw new IllegalStateException("User already exists!");
    } else {
      cache.put(user.getUsername(), user);
      write();
    }
  }

  @Override
  public void updateUser(User user) {
    cache.put(user.getUsername(), user);
    write();
  }

  @Override
  public void deleteUser(User user) {
    cache.remove(user.getUsername());
    write();
  }

  

}
