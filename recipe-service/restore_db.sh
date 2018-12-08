#!/bin/bash

docker exec $(docker ps --filter name=recipes_db -q) psql -U postgres --set ON_ERROR_STOP=on postgres

