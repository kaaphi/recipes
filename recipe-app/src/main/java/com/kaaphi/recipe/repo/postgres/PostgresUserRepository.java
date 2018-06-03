package com.kaaphi.recipe.repo.postgres;

import com.google.inject.Inject;
import com.kaaphi.recipe.repo.RecipeRepositoryException;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;
import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;

public class PostgresUserRepository extends AbstractPostgresRepository implements UserRepository {

  @Inject
  public PostgresUserRepository(DataSource ds) {
    super(ds);
  }
  
  @Override
  public User getUserByUsername(String username) {
    return executeQuery("SELECT id, username, password FROM getUser(?)", 
        stmt -> {
          stmt.setString(1, username);
        },
        rs -> {
          if(rs.next()) {
            int id = rs.getInt(1);
            String password = rs.getString(3);

            return new DbUser(id, rs.getString(2), password);
          } else {
            return null;
          }  
        });
  }

  @Override
  public User addUser(User user) {
    return executeQuery("SELECT addUser(?, ?)", 
        stmt -> {
          stmt.setString(1, user.getUsername());
          stmt.setString(2, user.getAuthDetails(PasswordAuthentication.PASSWORD_TYPE));
        }, 
        rs -> {
          if(rs.next()) {
            return new DbUser(rs.getInt(1), user.getUsername(), user.getAuthDetails());
          } else {
            throw new RecipeRepositoryException("Failed to add user, no id returned!");
          }
        });
  }

  @Override
  public void updateUser(User user) {
    DbUser dbUser = getDbUser(user);
    if(dbUser == null) {
      throw new RecipeRepositoryException("User does not exist!");
    }
    
    executeCall("updateUser(?, ?)", stmt -> {
      stmt.setInt(1, dbUser.getId());
      stmt.setString(2, dbUser.getAuthDetails(PasswordAuthentication.PASSWORD_TYPE));
    });
  }

  @Override
  public void deleteUser(User user) {
    DbUser dbUser = getDbUser(user);
    if(dbUser == null) {
      //already gone
      return;
    }
    
    executeCall("deleteUser(?)", stmt -> {
      stmt.setInt(1, dbUser.getId());
    });
  }
  
  private DbUser getDbUser(User user) {
    if(user instanceof DbUser) {
      return (DbUser) user;
    } else {
      return (DbUser) getUserByUsername(user.getUsername());
    }
  }
  
  static class DbUser extends User {
    private final int id;
    
    public DbUser(int id, String username, Map<String,String> authDetails) {
      super(username, authDetails);
      this.id = id;
    }
    
    public DbUser(int id, String username, String password) {
      this(id, username, Collections.singletonMap(PasswordAuthentication.PASSWORD_TYPE, password));
    }
    
    public int getId() {
      return id;
    }
    
  }

}
