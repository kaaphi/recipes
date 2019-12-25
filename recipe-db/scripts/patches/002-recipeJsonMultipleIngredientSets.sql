BEGIN;
SELECT _v.register_patch('002-recipeJsonMultipleIngredientSets', ARRAY ['001-alterRecipeSchema']);

UPDATE recipes SET recipe = jsonb_set(recipe, '{ingredientLists}',jsonb_build_array(jsonb_set('{}'::jsonb, '{ingredients}', recipe->'ingredients'))) - 'ingredients';

COMMIT;