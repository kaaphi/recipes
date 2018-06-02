package com.kaaphi.recipe.repo.postgres;

import com.kaaphi.recipe.repo.RecipeRepositoryException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import javax.sql.DataSource;

public class AbstractPostgresRepository {
  private DataSource ds;
  
  public AbstractPostgresRepository(DataSource ds) {
    this.ds = ds;
  }
  
  protected void executeUpdate(String preparedStatement, SqlConsumer<PreparedStatement> setParams) {
    try(Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(preparedStatement)) {
      setParams.accept(stmt);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  protected void executeCall(String preparedStatement, SqlConsumer<CallableStatement> setParams) {
    try(Connection conn = ds.getConnection(); CallableStatement stmt = conn.prepareCall(String.format("{ call %s }", preparedStatement))) {
      setParams.accept(stmt);
      stmt.execute();
    } catch (SQLException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  protected <R> R executeQuery(String preparedStatement, SqlConsumer<PreparedStatement> setParams, SqlFunction<ResultSet, R> function) {
    try(Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(preparedStatement)) {
      setParams.accept(stmt);
      try(ResultSet rs = stmt.executeQuery()) {
        return function.apply(rs);
      }
    } catch (SQLException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  protected <R> Stream<R> executeQueryStream(String preparedStatement, SqlConsumer<PreparedStatement> setParams, SqlFunction<ResultSet, R> rowFunction) {
    Stream.Builder<R> builder = Stream.builder();
    try(Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(preparedStatement)) {
      setParams.accept(stmt);
      try(ResultSet rs = stmt.executeQuery()) {
        while(rs.next()) {
          builder.accept(rowFunction.apply(rs));
        }
      }
      
      return builder.build();
    } catch (SQLException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  protected void executeQuery(String preparedStatement, SqlConsumer<PreparedStatement> setParams, SqlConsumer<ResultSet> consumeResultSet) {
    try(Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(preparedStatement)) {
      setParams.accept(stmt);
      consumeResultSet.accept(stmt.executeQuery());
    } catch (SQLException e) {
      throw new RecipeRepositoryException(e);
    }
  }
  
  @FunctionalInterface
  protected interface SqlConsumer<T> {
    void accept(T arg) throws SQLException;
  }
  
  @FunctionalInterface
  protected interface SqlFunction<T,R> {
    R apply(T arg) throws SQLException;
  }
}
