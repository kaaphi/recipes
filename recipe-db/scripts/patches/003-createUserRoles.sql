BEGIN;
SELECT _v.register_patch('003-createUserRoles', ARRAY ['000-createUsers']);
CREATE TABLE UserRoles (id SERIAL PRIMARY KEY, role VARCHAR(64) UNIQUE);

CREATE TABLE UserRolesForUser (
  id SERIAL PRIMARY KEY,
  userId INTEGER REFERENCES Users (id) NOT NULL,
  roleId INTEGER REFERENCES UserRoles (id) NOT NULL,
  CONSTRAINT u_uniqueRole UNIQUE (userId, roleId)
);

CREATE INDEX UserRolesForUser_userId ON UserRolesForUser (userId, roleId);

INSERT INTO UserRoles (role) VALUES
  ('USER'),
  ('ADMIN');

INSERT INTO UserRolesForUser (userId, roleId)
  SELECT Users.id, UserRoles.id FROM Users, UserRoles WHERE UserRoles.role = 'USER';

COMMIT;