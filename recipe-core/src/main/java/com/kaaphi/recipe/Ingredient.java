package com.kaaphi.recipe;

public class Ingredient {
  private final String name;
  private final String quantity;

  public Ingredient(String name, String quantity) {
    super();
    this.name = name;
    this.quantity = quantity;
  }

  public String getName() {
    return name;
  }

  public String getQuantity() {
    return quantity;
  }
}
