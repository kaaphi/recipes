package com.kaaphi.recipe.app;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.LongTermAuthPair;
import com.kaaphi.recipe.users.auth.LongTermAuthPair.LongTermAuthTokenClient;
import com.kaaphi.recipe.users.auth.LongTermAuthPair.LongTermAuthTokenServer;
import com.kaaphi.recipe.users.auth.LongTermAuthRepository;
import io.javalin.Context;
import io.javalin.builder.CookieBuilder;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongTermAuthController {
  private static final Logger log = LoggerFactory.getLogger(LongTermAuthController.class);
  
  private static final String REMEMBER_TOKEN = "rememberToken";
  
  private UserRepository userRepo;
  private LongTermAuthRepository repo;
  private TemporalAmount expiration;
  
  @Inject
  public LongTermAuthController(LongTermAuthRepository repo, UserRepository userRepo, @Named("longTermAuthExpireDays") String expireDays) {
    this.repo = repo;
    this.userRepo = userRepo;
    expiration = Duration.ofDays(Long.parseLong(expireDays));
  }
  
  public User validateLongTermAuth(Context ctx) {
    String tokenString = ctx.cookie(REMEMBER_TOKEN);
    log.trace("Found long term auth token {}", tokenString);
    if(tokenString != null) {
      LongTermAuthTokenClient clientToken = LongTermAuthPair.parseClientToken(tokenString);
      LongTermAuthTokenServer serverToken = repo.getServerToken(clientToken.getSelector());
      if(serverToken != null) {
        log.trace("Found server token.");
        if(serverToken.validate(clientToken) && serverToken.getExpires().isAfter(Instant.now())) {
          User user = userRepo.getUserByUsername(serverToken.getUsername());
          if(user != null) {
            //write new token
            writeNewToken(LongTermAuthPair.generatePair(serverToken.getUsername(), Instant.now().plus(expiration)), ctx);
            return user;
          }
        } else {
          log.trace("Server token invalid or expired!");
          repo.deleteServerToken(serverToken);
        }
      } else {
        log.trace("No matching server token, will clear cookie!");
        clearCookie(ctx);
      }
    }

    return null;
  }
  
  public void saveNewSession(User user, Context ctx) {
    writeNewToken(LongTermAuthPair.generatePair(user.getUsername(), Instant.now().plus(expiration)), ctx);
  }
  
  public void removeExisingSession(Context ctx) {
    String tokenString = ctx.cookie(REMEMBER_TOKEN);
    if(tokenString != null) {
      LongTermAuthTokenClient clientToken = LongTermAuthPair.parseClientToken(tokenString);
      repo.deleteServerToken(clientToken.getSelector());
      clearCookie(ctx);
    }
  }
  
  private void clearCookie(Context ctx) {
    ctx.cookie(CookieBuilder.cookieBuilder(REMEMBER_TOKEN, "")
        .httpOnly(true)
        //TODO secure(true)
        .maxAge(0)
        );
  }
  
  private void writeNewToken(LongTermAuthPair pair, Context ctx) {
    repo.saveServerToken(pair.getServer());
    ctx.cookie(CookieBuilder.cookieBuilder(REMEMBER_TOKEN, pair.getClient().toString())
        .maxAge(Math.toIntExact(Instant.now().until(pair.getServer().getExpires(), ChronoUnit.SECONDS)))
        .httpOnly(true)
        //TODO .secure(true)
        );
  }
}
