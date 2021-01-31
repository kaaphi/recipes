#!/bin/bash

cd ..
./gradlew recipe-db:build
cd recipe-db

docker container stop test-recipe-db
docker container rm test-recipe-db

docker run \
--name test-recipe-db \
--network recipe-net \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mysecretpassword \
--mount type=bind,source=`pwd`/build/scripts,target=/docker-entrypoint-initdb.d \
postgres:10.4-alpine
