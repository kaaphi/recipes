#!/bin/bash

docker exec -i $(docker ps --filter name=recipes_db -q) psql -U postgres --set ON_ERROR_STOP=on postgres

