#!/bin/bash

sudo docker run \
--rm \
--mount source=recipes_dbdata,destination=/var/lib/postgresql/data,readonly \
-v $(pwd):/backup ubuntu tar cvf /backup/backup.tar /var/lib/postgresql/data

