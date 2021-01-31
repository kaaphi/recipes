BEGIN;

DROP FUNCTION IF EXISTS insertOrUpdateRecipe;
CREATE OR REPLACE FUNCTION insertOrUpdateRecipe ( IN in_userId INTEGER, IN in_id UUID, IN in_recipe JSONB )
RETURNS VOID AS $$
	INSERT INTO Recipes (id, userId, recipe) VALUES (in_id, in_userId, in_recipe)
	ON CONFLICT (id) DO UPDATE SET recipe = EXCLUDED.recipe, updatedTime = CURRENT_TIMESTAMP WHERE Recipes.userId = EXCLUDED.userId
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS deleteRecipes;
CREATE OR REPLACE FUNCTION deleteRecipes ( IN in_userId INTEGER, IN in_ids UUID[] )
RETURNS VOID AS $$
	DELETE FROM Recipes WHERE userId = in_userId AND id = ANY (in_ids)
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS archiveRecipes;
CREATE OR REPLACE FUNCTION archiveRecipes ( IN in_userId INTEGER, IN in_ids UUID[], IN in_archiveSetting BOOLEAN DEFAULT true )
RETURNS VOID AS $$
	UPDATE Recipes SET isArchived = in_archiveSetting WHERE userId = in_userId AND id = ANY (in_ids)
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getRecipe;
CREATE OR REPLACE FUNCTION getRecipe ( IN in_userId INTEGER, IN in_id UUID )
RETURNS TABLE ( id UUID, recipe JSONB, createdTime TIMESTAMP, updatedTime TIMESTAMP, userId INTEGER, username VARCHAR(256), isArchived BOOLEAN ) AS $$
	SELECT r.id, r.recipe, r.createdTime, r.updatedTime, u.id, u.username, r.isArchived FROM Recipes r
	JOIN Users u ON r.userId = u.id
	LEFT JOIN ShareRecipes sr ON sr.fromUserId = r.userId AND sr.toUserId = in_userId
	WHERE COALESCE(sr.toUserId, r.userId) = in_userId AND r.id = in_id
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getAllRecipes;
CREATE OR REPLACE FUNCTION getAllRecipes ( IN in_userId INTEGER, IN in_includeArchived BOOLEAN DEFAULT false )
RETURNS TABLE ( id UUID, recipe JSONB, createdTime TIMESTAMP, updatedTime TIMESTAMP, userId INTEGER, username VARCHAR(256), isArchived BOOLEAN ) AS $$
	WITH userRecipes AS (
		SELECT id, userId, recipe, createdTime, updatedTime, isArchived FROM Recipes r
		WHERE userId = in_userId AND (in_includeArchived OR isArchived = false)
		UNION
		SELECT r.id, r.userId, r.recipe, r.createdTime, r.updatedTime, isArchived FROM Recipes r
		JOIN ShareRecipes sr ON sr.fromUserId = r.userid
		WHERE sr.toUserId = in_userId AND isArchived = false
	)
	SELECT ur.id, ur.recipe, ur.createdTime, ur.updatedTime, u.id, u.username, ur.isArchived  FROM userRecipes ur
	JOIN Users u ON u.id = ur.userId
	ORDER BY recipe->'title'
$$ LANGUAGE sql;

COMMIT;