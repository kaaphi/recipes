Param (
    [switch]$RebuildDB = $false,
    [string]$Network = "recipe-net"
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
--mount type=bind,source=$(Resolve-Path test-data),target=/docker-entrypoint-initdb.d `
postgres:10.4-alpine
