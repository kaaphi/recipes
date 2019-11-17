BEGIN;

CREATE OR REPLACE FUNCTION insertOrUpdateRecipe ( IN in_userId INTEGER, IN in_id UUID, IN in_recipe JSONB )
RETURNS VOID AS $$
	INSERT INTO Recipes (id, userId, recipe) VALUES (in_id, in_userId, in_recipe)
	ON CONFLICT (id) DO UPDATE SET recipe = EXCLUDED.recipe
$$ LANGUAGE sql;

CREATE OR REPLACE FUNCTION deleteRecipes ( IN in_userId INTEGER, IN in_ids UUID[] )
RETURNS VOID AS $$
	DELETE FROM Recipes WHERE userId = in_userId AND id = ANY (in_ids)
$$ LANGUAGE sql;

CREATE OR REPLACE FUNCTION getRecipe ( IN in_userId INTEGER, IN in_id UUID )
RETURNS TABLE ( recipe JSONB ) AS $$
	SELECT recipe FROM Recipes WHERE userId = in_userId AND id = in_id
$$ LANGUAGE sql;

CREATE OR REPLACE FUNCTION getAllRecipes ( IN in_userId INTEGER )
RETURNS TABLE ( recipe JSONB ) AS $$
	SELECT recipe FROM Recipes 
	WHERE userId = in_userId 
	UNION
	SELECT recipe FROM Recipes
	JOIN ShareRecipes ON Recipe.userId = ShareRecipes.fromUserId
	WHERE ShareRecipes.toUserId = in_userId
	ORDER BY recipe->'recipe'->'title'
$$ LANGUAGE sql;

COMMIT;