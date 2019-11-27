#!/bin/sh

for f in /dbscripts/*; do
    echo Running $f
    psql -U postgres --set ON_ERROR_STOP=on postgres -f $f
done
