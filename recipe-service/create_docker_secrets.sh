#!/bin/bash

password=$1
dbUrl="jdbc:postgresql://recipes_db/postgres?user=postgres&password=$1"

docker secret rm postgres-passwd
docker secret rm dbUrl

echo $password | docker secret create postgres-passwd -
echo $dbUrl | docker secret create dbUrl -

