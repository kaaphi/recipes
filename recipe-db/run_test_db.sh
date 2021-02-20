#!/bin/bash

initPath="build/scripts"
if [ "$1" == "-b" ]; then
  if [ "$2" != "" ]; then
    initPath=$2
  else
    initPath="test-data"
  fi
fi

set -e

cd ..
./gradlew recipe-db:build
cd recipe-db

set +e
docker container stop test-recipe-db
docker container rm test-recipe-db
set -e

docker run \
--name test-recipe-db \
-d \
-p 5432:5432 \
-e POSTGRES_PASSWORD=mysecretpassword \
--mount type=bind,source=`readlink -f $initPath`,target=/docker-entrypoint-initdb.d \
--mount type=bind,source=`readlink -f build/scripts`,target=/dbscripts \
postgres:10.4-alpine

sleep 5

if [ "$1" = "-b" ]; then
  docker exec \
  test-recipe-db \
  /bin/sh -c 'for f in /dbscripts/*; do echo Running $f; psql -U postgres --set ON_ERROR_STOP=on postgres -f $f; done'
fi  