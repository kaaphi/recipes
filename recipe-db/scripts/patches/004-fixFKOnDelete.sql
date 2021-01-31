BEGIN;
SELECT _v.register_patch('004-fixFKOnDelete', ARRAY ['000-createRecipes', '003-createUserRoles', '001-shareRecipes']);

ALTER TABLE UserRolesForUser
DROP CONSTRAINT userrolesforuser_userid_fkey,
ADD CONSTRAINT userrolesforuser_userid_fkey
   FOREIGN KEY (userId)
   REFERENCES Users (id)
   ON DELETE CASCADE;

ALTER TABLE Recipes
DROP CONSTRAINT recipes_userid_fkey,
ADD CONSTRAINT recipes_userid_fkey
   FOREIGN KEY (userId)
   REFERENCES Users (id)
   ON DELETE CASCADE;

ALTER TABLE ShareRecipes
DROP CONSTRAINT sharerecipes_fromuserid_fkey,
ADD CONSTRAINT sharerecipes_fromuserid_fkey
   FOREIGN KEY (fromUserId)
   REFERENCES Users (id)
   ON DELETE CASCADE;

ALTER TABLE ShareRecipes
DROP CONSTRAINT sharerecipes_touserid_fkey,
ADD CONSTRAINT sharerecipes_touserid_fkey
   FOREIGN KEY (toUserId)
   REFERENCES Users (id)
   ON DELETE CASCADE;

COMMIT;