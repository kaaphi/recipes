BEGIN;
SELECT _v.register_patch('001-shareRecipes', ARRAY ['000-createUsers']);
CREATE TABLE ShareRecipes (
    id SERIAL PRIMARY KEY,
    fromUserId INTEGER REFERENCES Users (id),
    toUserId INTEGER REFERENCES Users (id)
);
CREATE INDEX ShareRecipes_userId ON ShareRecipes (fromUserId, toUserId);
COMMIT;