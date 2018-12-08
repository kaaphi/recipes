#!/bin/bash

docker exec $(docker ps --filter name=recipes_db -q) pg_dump -d postgres -U postgres > backup_`date +%Y%m%d`.dump

