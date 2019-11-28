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
RETURNS TABLE ( id UUID, recipe JSONB, createdTime TIMESTAMP, updatedTime TIMESTAMP, userId INTEGER, username VARCHAR(256) ) AS $$
	SELECT r.id, r.recipe, r.createdTime, r.updatedTime, u.id, u.username FROM Recipes r
	JOIN Users u ON r.userId = u.id
	WHERE r.userId = in_userId AND r.id = in_id
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getAllRecipes;
CREATE OR REPLACE FUNCTION getAllRecipes ( IN in_userId INTEGER )
RETURNS TABLE ( id UUID, recipe JSONB, createdTime TIMESTAMP, updatedTime TIMESTAMP, userId INTEGER, username VARCHAR(256) ) AS $$
	WITH userRecipes AS (
		SELECT id, userId, recipe, createdTime, updatedTime FROM Recipes r
		WHERE userId = in_userId
		UNION
		SELECT r.id, r.userId, r.recipe, r.createdTime, r.updatedTime FROM Recipes r
		JOIN ShareRecipes sr ON sr.fromUserId = r.userid
		WHERE sr.toUserId = in_userId
	)
	SELECT ur.id, ur.recipe, ur.createdTime, ur.updatedTime, u.id, u.username  FROM userRecipes ur
	JOIN Users u ON u.id = ur.userId
	ORDER BY recipe->'title'
$$ LANGUAGE sql;

COMMIT;