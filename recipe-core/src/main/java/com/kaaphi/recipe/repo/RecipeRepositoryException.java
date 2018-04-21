package com.kaaphi.recipe.repo;

public class RecipeRepositoryException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public RecipeRepositoryException() {}

  public RecipeRepositoryException(String arg0) {
    super(arg0);
  }

  public RecipeRepositoryException(Throwable arg0) {
    super(arg0);
  }

  public RecipeRepositoryException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  public RecipeRepositoryException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
    super(arg0, arg1, arg2, arg3);
  }

}
