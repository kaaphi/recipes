package com.kaaphi.recipe.repo.postgres;

import com.google.inject.Inject;
import com.kaaphi.recipe.repo.RecipeRepositoryException;
import com.kaaphi.recipe.users.AuthenticatableUser;
import com.kaaphi.recipe.users.User;
import com.kaaphi.recipe.users.UserRepository;
import com.kaaphi.recipe.users.UserRole;
import com.kaaphi.recipe.users.UserShare;
import com.kaaphi.recipe.users.auth.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;

public class PostgresUserRepository extends AbstractPostgresRepository implements UserRepository {

  @Inject
  public PostgresUserRepository(DataSource ds) {
    super(ds);
  }
  
  @Override
  public List<User> getAll() {
    return executeQuery("SELECT id, username FROM Users",
        __ -> {}, 
        rs -> {
          List<User> users = new ArrayList<>();
          while(rs.next()) {
            users.add(new DbUser(rs.getInt(1), rs.getString(2)));
          }
          return users;
        });
  }
  
  @Override
  public User getUserByUsername(String username) {
    return Optional.ofNullable(getAuthenticatableUser(username)).map(AuthenticatableUser::getUser).orElse(null);
  }

  @Override
  public AuthenticatableUser addUser(AuthenticatableUser user) {
    return executeQuery("SELECT addUser(?, ?)", 
        stmt -> {
          stmt.setString(1, user.getUser().getUsername());
          stmt.setString(2, user.getAuthDetails(PasswordAuthentication.PASSWORD_TYPE));
        }, 
        rs -> {
          if(rs.next()) {
            return new AuthenticatableUser(new DbUser(rs.getInt(1), user.getUser().getUsername()), user.getAuthDetails());
          } else {
            throw new RecipeRepositoryException("Failed to add user, no id returned!");
          }
        });
  }

  @Override
  public void updateUser(User user) {
    //no-op, no editable properties
  }
  
  

  @Override
  public AuthenticatableUser getAuthenticatableUser(String username) {
    return executeQuery("SELECT id, username, password FROM getUser(?)", 
        stmt -> {
          stmt.setString(1, username);
        },
        rs -> {
          if(rs.next()) {
            int id = rs.getInt(1);
            String password = rs.getString(3);

            return new AuthenticatableUser(new DbUser(id, rs.getString(2)), Collections.singletonMap(PasswordAuthentication.PASSWORD_TYPE, password));
          } else {
            return null;
          }  
        });
  }

  @Override
  public void setAuthDetails(User user, Map<String, String> details) {
    DbUser dbUser = getDbUser(user);
    if(dbUser == null) {
      throw new RecipeRepositoryException("User does not exist!");
    }

    executeCall("updateUser(?, ?)", stmt -> {
      stmt.setInt(1, dbUser.getId());
      stmt.setString(2, details.get(PasswordAuthentication.PASSWORD_TYPE));
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
  
  @Override
  public List<UserShare> getSharesForUser(User user) {
    DbUser dbUser = getDbUser(user);
    return executeQuery("SELECT id, username FROM getUserShare(?)", 
        stmt -> {
          stmt.setInt(1, dbUser.getId());
        },
        rs -> {
          List<UserShare> shares = new ArrayList<>();
          while(rs.next()) {
            shares.add(new UserShare(user, new DbUser(rs.getInt(1), rs.getString(2))));
          }
          return shares;
        });
  }

  @Override
  public void addUserShare(UserShare share) {
    DbUser fromUser = getDbUser(share.getFromUser());
    DbUser toUser = getDbUser(share.getToUser());
    executeCall("addUserShare(?,?)", stmt -> {
      stmt.setInt(1, fromUser.getId());
      stmt.setInt(2, toUser.getId());
    });
  }

  @Override
  public void deleteUserShare(UserShare share) {
    DbUser fromUser = getDbUser(share.getFromUser());
    DbUser toUser = getDbUser(share.getToUser());
    executeCall("deleteUserShare(?,?)", stmt -> {
      stmt.setInt(1, fromUser.getId());
      stmt.setInt(2, toUser.getId());
    });
  }

  @Override
  public Set<UserRole> getRolesForUser(User user) {
    DbUser dbUser = getDbUser(user);
    return executeQuery("SELECT role FROM getUserRoles(?)",
        stmt -> {
          stmt.setInt(1, dbUser.getId());
        },
        rs -> {
          Set<UserRole> roles = new HashSet<>();
          while(rs.next()) {
            roles.add(UserRole.valueOf(rs.getString(1)));
          }
          return roles;
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
    
    public DbUser(int id, String username) {
      super(username);
      this.id = id;
    }
    
    public int getId() {
      return id;
    }
    
  }

}
