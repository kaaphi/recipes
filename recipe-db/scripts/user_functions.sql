BEGIN;

CREATE OR REPLACE FUNCTION addUser ( IN username VARCHAR(256), IN password TEXT )
RETURNS INTEGER AS $$
	INSERT INTO Users (username, password) VALUES (username, password) RETURNING id;
$$ LANGUAGE sql;

CREATE OR REPLACE FUNCTION updateUser ( IN id INTEGER, IN password TEXT )
RETURNS VOID AS $$
	UPDATE Users SET password = password WHERE id = id
$$ LANGUAGE sql;

CREATE OR REPLACE FUNCTION deleteUser ( IN id INTEGER )
RETURNS VOID AS $$
	DELETE FROM Users WHERE id = id 
$$ LANGUAGE sql;

CREATE OR REPLACE FUNCTION getUser ( IN username VARCHAR(256) )
RETURNS TABLE ( id INTEGER, username VARCHAR(256), password TEXT ) AS $$
	SELECT id, username, password FROM Users WHERE username = username
$$ LANGUAGE sql;



COMMIT;