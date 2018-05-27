package com.kaaphi.recipe.users.auth;

import com.kaaphi.recipe.users.auth.LongTermAuthPair.LongTermAuthTokenServer;

public interface LongTermAuthRepository {
  LongTermAuthTokenServer getServerToken(String selector);
  void saveServerToken(LongTermAuthTokenServer token);
  void deleteServerToken(String selector);
  default void deleteServerToken(LongTermAuthTokenServer token) {
    deleteServerToken(token.getSelector());
  }
  
}
