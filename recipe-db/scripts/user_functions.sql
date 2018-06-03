BEGIN;

DROP FUNCTION addUser;
CREATE OR REPLACE FUNCTION addUser ( IN in_username VARCHAR(256), IN in_password TEXT )
RETURNS INTEGER AS $$
	INSERT INTO Users (username, password) VALUES (in_username, in_password) RETURNING id;
$$ LANGUAGE sql;

DROP FUNCTION updateUser;
CREATE OR REPLACE FUNCTION updateUser ( IN in_id INTEGER, IN in_password TEXT )
RETURNS VOID AS $$
	UPDATE Users SET password = in_password WHERE id = in_id
$$ LANGUAGE sql;

DROP FUNCTION deleteUser;
CREATE OR REPLACE FUNCTION deleteUser ( IN in_id INTEGER )
RETURNS VOID AS $$
	DELETE FROM Users WHERE id = in_id 
$$ LANGUAGE sql;

DROP FUNCTION getUser;
CREATE OR REPLACE FUNCTION getUser ( IN in_username VARCHAR(256) )
RETURNS TABLE ( id INTEGER, username VARCHAR(256), password TEXT ) AS $$
	SELECT id, username, password FROM Users WHERE username = in_username
$$ LANGUAGE sql;



COMMIT;