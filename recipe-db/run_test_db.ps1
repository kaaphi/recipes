Param (
    [switch]$RebuildDB = $false,
    [string]$Network = "recipe-net",
    [string]$BackupPath = "test-data"
)

Write-Host "Stopping and removing container if necessary."
docker container stop test-recipe-db
docker container rm test-recipe-db

if($RebuildDB) {
  Write-Host "Deleting volume."
  docker volume rm recipes_test_db
}

Write-Host "Starting container."
docker run `
--name test-recipe-db `
-d `
-p 5432:5432 `
-e POSTGRES_PASSWORD=mysecretpassword `
--network="$Network" `
--mount type=volume,source=recipes_test_db,target=/var/lib/postgresql/data `
--mount type=bind,source=$(Resolve-Path $BackupPath),target=/docker-entrypoint-initdb.d `
--mount type=bind,source=$(Resolve-Path build\scripts),target=/dbscripts `
postgres:10.4-alpine

Start-Sleep -Seconds 2

docker exec `
test-recipe-db `
/bin/sh -c 'for f in /dbscripts/*; do echo Running $f; psql -U postgres --set ON_ERROR_STOP=on postgres -f $f; done'

