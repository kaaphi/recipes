package com.kaaphi.recipe;

import java.util.Objects;
import java.util.Optional;

public class Ingredient {
  private final String name;
  private final String quantity;
  
  public Ingredient(String name) {
    this(name, null);
  }
  
  public Ingredient(String name, String quantity) {
    this.name = name;
    this.quantity = quantity;
  }

  public String getName() {
    return name;
  }

  public Optional<String> getQuantity() {
    return Optional.ofNullable(quantity);
  }
  
  public boolean equals(Object o) {
    if(o instanceof Ingredient) {
      Ingredient that = (Ingredient)o;
      return Objects.equals(this.name, that.name) && Objects.equals(this.quantity, that.quantity);
    } else {
      return false;
    }
  }
  
  public int hashCode() {
    return Objects.hash(name, quantity);
  }
  
  public String toString() {
    return String.format("Ingredient[<%s> <%s>]", quantity, name);
  }
}
