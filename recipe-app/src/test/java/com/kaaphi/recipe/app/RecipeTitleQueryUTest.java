package com.kaaphi.recipe.app;

import static org.junit.Assert.assertEquals;

import com.kaaphi.recipe.app.TypeaheadSearchController.RecipeTitleQuery;
import com.kaaphi.recipe.app.TypeaheadSearchController.RecipeTitleResult;
import com.kaaphi.recipe.app.TypeaheadSearchController.RecipeTitleResultType;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RecipeTitleQueryUTest {

  @Parameters(name = "{index} : {0}({1}) -> {2}")
  public static Object[][] data() {
    return new Object[][] {
        {"app", "apple", RecipeTitleResultType.STARTS_WITH},
        {"app", "big apple", RecipeTitleResultType.TOKEN_STARTS_WITH},
        {"app", "pineapple", RecipeTitleResultType.CONTAINS},
        {"app", "potato", null}
    };
  }

  private String query;
  private String title;
  private Optional<RecipeTitleResultType> expected;

  public RecipeTitleQueryUTest(String query, String title,
      RecipeTitleResultType expected) {
    this.query = query;
    this.title = title;
    this.expected = Optional.ofNullable(expected);
  }

  @Test
  public void test() {
    assertEquals(expected, new RecipeTitleQuery(query)
        .apply(title)
        .map(RecipeTitleResult::getResultType));
  }

}
