BEGIN;
SELECT _v.register_patch('000-createUsers');
CREATE TABLE Users (id SERIAL PRIMARY KEY, username VARCHAR(256) UNIQUE, password text );
COMMIT;