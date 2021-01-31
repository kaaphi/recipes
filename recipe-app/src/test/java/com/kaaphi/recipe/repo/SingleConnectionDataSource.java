package com.kaaphi.recipe.repo;

import com.kaaphi.recipe.repo.RepoTestHelper.RepoTestTransaction;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class SingleConnectionDataSource implements DataSource, RepoTestTransaction {
  private Connection connection;
  private DataSource delegate;

  public SingleConnectionDataSource(DataSource delegate) throws SQLException {
    this.delegate = delegate;
    final Connection connectionDelegate = delegate.getConnection();
    connectionDelegate.setAutoCommit(false);
    connection = (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class},
        (proxy, method, args) -> {
          if("close".equals(method.getName())) {
            return null;
          } else {
            return method.invoke(connectionDelegate, args);
          }
        });
  }

  @Override
  public void beginTransaction() {
    //noop
  }

  @Override
  public void rollbackTransaction() throws Exception {
    connection.rollback();
  }

  @Override
  public Connection getConnection() throws SQLException {
    return connection;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return connection;
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return delegate.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    delegate.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    delegate.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return delegate.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return delegate.getParentLogger();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return delegate.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return delegate.isWrapperFor(iface);
  }
}
