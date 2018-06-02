#!/bin/bash

sudo docker run \
--name test-recipe-db \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mysecretpassword \
--mount type=bind,source="$(pwd)"/testDb,target=/var/lib/postgresql/data \
recipe-db
