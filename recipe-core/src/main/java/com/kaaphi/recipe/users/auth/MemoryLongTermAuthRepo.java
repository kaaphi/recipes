package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.users.auth.LongTermAuthPair.LongTermAuthTokenServer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryLongTermAuthRepo implements LongTermAuthRepository {
  private Map<String, LongTermAuthTokenServer> tokenStore = new ConcurrentHashMap<>();
  
  @Override
  public LongTermAuthTokenServer getServerToken(String selector) {
    return tokenStore.get(selector);
  }

  @Override
  public void saveServerToken(LongTermAuthTokenServer token) {
    tokenStore.put(token.getSelector(), token);
  }

  @Override
  public void deleteServerToken(String selector) {
    tokenStore.remove(selector);
  }

}
