package com.kaaphi.recipe.users.auth;

import com.google.inject.Inject;
import com.kaaphi.recipe.users.auth.LongTermAuthPair.LongTermAuthTokenServer;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

public class RedisLongTermAuthRepo implements LongTermAuthRepository {
  private JedisPool jedisPool;

  @Inject
  public RedisLongTermAuthRepo(JedisPool jedisPool) {
    this.jedisPool = jedisPool;
  }

  @Override
  public LongTermAuthTokenServer getServerToken(String selector) {
    try(Jedis jedis = jedisPool.getResource()) {
      return Optional.ofNullable(jedis.get(getKeyFromSelector(selector)))
          .map(LongTermAuthPair::parseServerToken)
          .orElse(null);
    }
  }

  @Override
  public void saveServerToken(LongTermAuthTokenServer token) {
    try(Jedis jedis = jedisPool.getResource()) {
      long expiryMs = Duration.between(Instant.now(), token.getExpires()).toMillis();
      jedis.set(getKeyFromSelector(token.getSelector()), token.toString(), SetParams.setParams().px(expiryMs));
    }
  }

  @Override
  public void deleteServerToken(String selector) {
    try(Jedis jedis = jedisPool.getResource()) {
      jedis.del(getKeyFromSelector(selector));
    }
  }

  private static String getKeyFromSelector(String selector) {
    return "auth:" + selector;
  }
}
