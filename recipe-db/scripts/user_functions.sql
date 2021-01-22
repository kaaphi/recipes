BEGIN;

DROP FUNCTION IF EXISTS addUser;
CREATE OR REPLACE FUNCTION addUser ( IN in_username VARCHAR(256), IN in_password TEXT )
RETURNS INTEGER AS $$
	WITH userRow AS (
		INSERT INTO Users (username, password) VALUES (in_username, in_password) RETURNING id
	)
	, roleInsert AS (
		INSERT INTO UserRolesForUser (userId, roleId)
			SELECT userRow.id, (SELECT UserRoles.id FROM UserRoles WHERE UserRoles.role = 'USER') FROM userRow
	)
	SELECT id FROM userRow;
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS updateUser;
CREATE OR REPLACE FUNCTION updateUser ( IN in_id INTEGER, IN in_password TEXT )
RETURNS VOID AS $$
	UPDATE Users SET password = in_password WHERE id = in_id
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS deleteUser;
CREATE OR REPLACE FUNCTION deleteUser ( IN in_id INTEGER )
RETURNS VOID AS $$
	DELETE FROM Users WHERE id = in_id 
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getUser;
CREATE OR REPLACE FUNCTION getUser ( IN in_username VARCHAR(256) )
RETURNS TABLE ( id INTEGER, username VARCHAR(256), password TEXT ) AS $$
	SELECT id, username, password FROM Users WHERE username = in_username
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getUserShare;
CREATE OR REPLACE FUNCTION getUserShare ( IN in_id INTEGER )
RETURNS TABLE ( id INTEGER, username VARCHAR(256) ) AS $$
	SELECT tu.id, tu.username 
	FROM Users fu
	JOIN ShareRecipes sr ON sr.fromUserId = fu.id
	JOIN Users tu ON sr.toUserId = tu.id
	WHERE fu.id = in_id
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS addUserShare;
CREATE OR REPLACE FUNCTION addUserShare ( IN in_fromId INTEGER, IN in_toId INTEGER )
RETURNS VOID AS $$
	INSERT INTO ShareRecipes (fromUserId, toUserId) VALUES (in_fromId, in_toId)
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS deleteUserShare;
CREATE OR REPLACE FUNCTION deleteUserShare ( IN in_fromId INTEGER, IN in_toId INTEGER )
RETURNS VOID AS $$
	DELETE FROM ShareRecipes WHERE fromUserId = in_fromId AND toUserId = in_toId
$$ LANGUAGE sql;

DROP FUNCTION IF EXISTS getUserRoles;
CREATE OR REPLACE FUNCTION getUserRoles ( IN in_id INTEGER )
RETURNS TABLE ( role VARCHAR(64) ) AS $$
	SELECT r.role
	FROM UserRolesForUser ur
	JOIN UserRoles r ON ur.roleId = r.id
	WHERE ur.userId = in_id
$$ LANGUAGE sql;

COMMIT;