BEGIN;
SELECT _v.register_patch('001-alterRecipeSchema', ARRAY ['000-createRecipes']);
ALTER TABLE Recipes 
	ADD COLUMN createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	ADD COLUMN updatedTime TIMESTAMP
;

UPDATE Recipes SET 
createdTime = TIMESTAMP 'epoch' + (((recipe->'created'->>'seconds')::bigint * 1000) +  ((recipe->'created'->>'nanos')::bigint / 1000000)) * interval '1 millisecond',
updatedTime = TIMESTAMP 'epoch' + (((recipe->'updated'->>'seconds')::bigint * 1000) +  ((recipe->'updated'->>'nanos')::bigint / 1000000)) * interval '1 millisecond',
recipe = recipe->'recipe';

ALTER TABLE Recipes
	ALTER COLUMN createdTime SET NOT NULL;
COMMIT;