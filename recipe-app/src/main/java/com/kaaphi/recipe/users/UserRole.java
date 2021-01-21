package com.kaaphi.recipe.users;

import io.javalin.core.security.Role;

public enum UserRole implements Role {
  ANONYMOUS,
  USER,
  ADMIN
}
