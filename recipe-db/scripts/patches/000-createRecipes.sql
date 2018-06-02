BEGIN;
SELECT _v.register_patch('000-createRecipes', ARRAY ['000-createUsers']);
CREATE TABLE Recipes ( id UUID PRIMARY KEY, userId INTEGER REFERENCES Users (id), recipe JSONB );
CREATE INDEX Recipes_userId_id ON Recipes (userId, id);
COMMIT;