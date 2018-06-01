BEGIN;
SELECT _v.register_patch('000-createRecipes', ARRAY ['000-createUsers']);
CREATE TABLE Recipes ( id UUID, userId INTEGER REFERENCES Users (id), recipe JSONB );
COMMIT;