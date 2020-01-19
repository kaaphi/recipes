package com.kaaphi.recipe.txtformat;

import static org.junit.Assert.assertEquals;
import com.kaaphi.recipe.Ingredient;
import com.kaaphi.recipe.Recipe;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

public class TestTextFormat {
  private static final String TITLE = "My Title";
  private static final List<String> INGREDIENTS1 = Arrays.asList(
      "butter",
      "sugar"
  );

  private static final List<String> INGREDIENTS2 = Arrays.asList(
      "i1",
      "i2",
      "i3"
  );

  private static final List<String> SOURCES = Arrays.asList(
      "source1",
      "source2"
  );

  private static final String METHOD_LF =
      "Mix the butter\n"
          + "and the sugar\n"
          + "together\n";

  private static final String METHOD_CR_LF =
      "Mix the butter\r\n"
          + "and the sugar\r\n"
          + "together\r\n";

  @Test
  public void testBasicCompleteRecipe() {
    StringBuilder sb = new StringBuilder()
        .append(TITLE).append("\n\n");

    appendList(sb, INGREDIENTS1).append("\n")
        .append(METHOD_LF).append("\n")
        .append("SOURCES\n");

    appendList(sb, SOURCES);

    Recipe r = new TextFormat().fromText(sb.toString());

    assertEquals(TITLE, r.getTitle());
    assertEquals(1, r.getIngredientLists().size());
    assertEquals(Optional.empty(), r.getIngredientLists().get(0).getName());
    assertEquals(INGREDIENTS1, convertIngredientsToString(r, 0));
    assertEquals(METHOD_CR_LF, r.getMethod());
    assertEquals(SOURCES, r.getSources());
  }

  @Test
  public void testBasicRecipeNoSources() {
    StringBuilder sb = new StringBuilder()
        .append(TITLE).append("\n\n");

    appendList(sb, INGREDIENTS1).append("\n")
        .append(METHOD_LF).append("\n");

    Recipe r = new TextFormat().fromText(sb.toString());

    assertEquals(TITLE, r.getTitle());
    assertEquals(1, r.getIngredientLists().size());
    assertEquals(Optional.empty(), r.getIngredientLists().get(0).getName());
    assertEquals(INGREDIENTS1, convertIngredientsToString(r, 0));
    assertEquals(METHOD_CR_LF, r.getMethod());
    assertEquals(Collections.emptyList(), r.getSources());
  }

  @Test
  public void testBasicRecipeSingleLineMethodNoSourcesNoNewLines() {
    testBasicRecipeSingleLineMethodNoSources(0, 0);
  }

  @Test
  public void testBasicRecipeSingleLineMethodNoSourcesOneNewLine() {
    testBasicRecipeSingleLineMethodNoSources(1, 0);
  }

  @Test
  public void testBasicRecipeSingleLineMethodNoSourcesTwoNewLines() {
    testBasicRecipeSingleLineMethodNoSources(2, 1);
  }



  private void testBasicRecipeSingleLineMethodNoSources(int numNewlinesAfterMethod, int expectedNewLines) {
    StringBuilder sb = new StringBuilder()
        .append(TITLE).append("\n\n");

    appendList(sb, INGREDIENTS1).append("\n")
        .append("Method");
    for(int i = 0; i < numNewlinesAfterMethod; i++) {
      sb.append("\n");
    }

    Recipe r = new TextFormat().fromText(sb.toString());

    assertEquals(TITLE, r.getTitle());
    assertEquals(1, r.getIngredientLists().size());
    assertEquals(Optional.empty(), r.getIngredientLists().get(0).getName());
    assertEquals(INGREDIENTS1, convertIngredientsToString(r, 0));
    assertEquals("Method" + IntStream.range(0, expectedNewLines).mapToObj(i -> "\r\n").collect(Collectors.joining())
        , r.getMethod());
    assertEquals(Collections.emptyList(), r.getSources());
  }

  @Test
  public void testBasicRecipeNoMethodNoSources() {
    StringBuilder sb = new StringBuilder()
        .append(TITLE).append("\n\n");

    appendList(sb, INGREDIENTS1).append("\n");

    Recipe r = new TextFormat().fromText(sb.toString());

    assertEquals(TITLE, r.getTitle());
    assertEquals(1, r.getIngredientLists().size());
    assertEquals(Optional.empty(), r.getIngredientLists().get(0).getName());
    assertEquals(INGREDIENTS1, convertIngredientsToString(r, 0));
    assertEquals("", r.getMethod());
    assertEquals(Collections.emptyList(), r.getSources());
  }

  @Test
  public void testMultiIngredientListRecipeNoMethodNoSources() {
    StringBuilder sb = new StringBuilder()
        .append(TITLE).append("\n\n");

    appendList(sb, INGREDIENTS1).append("\n");
    sb.append("INGREDIENTS2:\n");
    appendList(sb, INGREDIENTS2).append("\n");

    Recipe r = new TextFormat().fromText(sb.toString());

    assertEquals(TITLE, r.getTitle());
    assertEquals(2, r.getIngredientLists().size());
    assertEquals(Optional.empty(), r.getIngredientLists().get(0).getName());
    assertEquals(INGREDIENTS1, convertIngredientsToString(r, 0));
    assertEquals(Optional.of("INGREDIENTS2:"), r.getIngredientLists().get(1).getName());
    assertEquals(INGREDIENTS2, convertIngredientsToString(r, 1));
    assertEquals("", r.getMethod());
    assertEquals(Collections.emptyList(), r.getSources());
  }

  @Test
  public void testMultipleIngredientListWithDefaultListRecipe() {
    StringBuilder sb = new StringBuilder()
        .append(TITLE).append("\n\n");

    appendList(sb, INGREDIENTS1).append("\n")
        .append("INGREDIENTS2:\n");
    appendList(sb, INGREDIENTS2).append("\n")
        .append(METHOD_LF).append("\n")
        .append("SOURCES\n");

    appendList(sb, SOURCES);

    Recipe r = new TextFormat().fromText(sb.toString());

    assertEquals(TITLE, r.getTitle());
    assertEquals(2, r.getIngredientLists().size());
    assertEquals(Optional.empty(), r.getIngredientLists().get(0).getName());
    assertEquals(INGREDIENTS1, convertIngredientsToString(r, 0));
    assertEquals(Optional.of("INGREDIENTS2:"), r.getIngredientLists().get(1).getName());
    assertEquals(INGREDIENTS2, convertIngredientsToString(r, 1));
    assertEquals(METHOD_CR_LF, r.getMethod());
    assertEquals(SOURCES, r.getSources());
  }

  @Test
  public void testMultipleIngredientListRecipe() {
    StringBuilder sb = new StringBuilder()
        .append(TITLE).append("\n\n")
        .append("INGREDIENTS1:\n");
    appendList(sb, INGREDIENTS1).append("\n")
        .append("INGREDIENTS2:\n");
    appendList(sb, INGREDIENTS2).append("\n")
        .append(METHOD_LF).append("\n")
        .append("SOURCES\n");
    appendList(sb, SOURCES);

    Recipe r = new TextFormat().fromText(sb.toString());

    assertEquals(TITLE, r.getTitle());
    assertEquals(2, r.getIngredientLists().size());
    assertEquals(Optional.of("INGREDIENTS1:"), r.getIngredientLists().get(0).getName());
    assertEquals(INGREDIENTS1, convertIngredientsToString(r, 0));
    assertEquals(Optional.of("INGREDIENTS2:"), r.getIngredientLists().get(1).getName());
    assertEquals(INGREDIENTS2, convertIngredientsToString(r, 1));
    assertEquals(METHOD_CR_LF, r.getMethod());
    assertEquals(SOURCES, r.getSources());
  }

  private static StringBuilder appendList(StringBuilder sb, List<String> str) {
    str.forEach(s -> sb.append(s).append("\n"));
    return sb;
  }

  private static List<String> convertIngredientsToString(Recipe r, int idx) {
    return r.getIngredientLists().get(idx).getIngredients().stream().map(Ingredient::getName).collect(
        Collectors.toList());
  }
}
