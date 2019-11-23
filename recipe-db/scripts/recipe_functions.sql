BEGIN;

DROP FUNCTION IF EXISTS insertOrUpdateRecipe;
CREATE OR REPLACE FUNCTION insertOrUpdateRecipe ( IN in_userId INTEGER, IN in_id UUID, IN in_recipe JSONB )
RETURNS VOID AS $$
	INSERT INTO Recipes (id, userId, recipe) VALUES (in_id, in_userId, in_recipe)
	ON CONFLICT (id) DO UPDATE SET recipe = EXCLUDED.recipe, updatedTime = CURRENT_TIMESTAMP
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS deleteRecipes;
CREATE OR REPLACE FUNCTION deleteRecipes ( IN in_userId INTEGER, IN in_ids UUID[] )
RETURNS VOID AS $$
	DELETE FROM Recipes WHERE userId = in_userId AND id = ANY (in_ids)
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getRecipe;
CREATE OR REPLACE FUNCTION getRecipe ( IN in_userId INTEGER, IN in_id UUID )
RETURNS TABLE ( id UUID, recipe JSONB, createdTime TIMESTAMP, updatedTime TIMESTAMP ) AS $$
	SELECT id, recipe, createdTime, updatedTime FROM Recipes WHERE userId = in_userId AND id = in_id
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getAllRecipes;
CREATE OR REPLACE FUNCTION getAllRecipes ( IN in_userId INTEGER )
RETURNS TABLE ( id UUID, recipe JSONB, createdTime TIMESTAMP, updatedTime TIMESTAMP ) AS $$
	SELECT id, recipe, createdTime, updatedTime FROM Recipes 
	WHERE userId = in_userId 
	ORDER BY recipe->'title'
$$ LANGUAGE sql;

COMMIT;