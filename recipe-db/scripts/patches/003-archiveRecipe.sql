BEGIN;
SELECT _v.register_patch('003-archiveRecipe', ARRAY ['000-createRecipes']);
ALTER TABLE Recipes 
	ADD COLUMN isArchived BOOLEAN NOT NULL DEFAULT FALSE
;

CREATE INDEX Recipes_userId_isArchive ON Recipes (userId, isArchived, id);

COMMIT;