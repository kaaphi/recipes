#!/bin/bash

sudo docker run \
--name test-recipe-db \
--network recipe-net \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mysecretpassword \
--mount type=volume,source=recipes_dbdata,target=/var/lib/postgresql/data \
recipe-db
