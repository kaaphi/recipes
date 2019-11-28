#!/bin/bash
set -e

push=false
registry="localhost:5000"
version="latest"

while getopts "r:v:p" opt; do
  case $opt in
    r) registry="$OPTARG"
    ;;
    v) version="$OPTARG"
    ;;
    p) push=true
    ;;
    ?) echo "Invalid option -$OPTARG" >&2
    ;;
  esac
done

project_root=`realpath "$(dirname "$0")"`

cd $project_root
./gradlew build

for p in "recipe-admin" "recipe-app" "recipe-db"; do
    echo $p
    cd $project_root/$p
    docker image build -t $registry/$p:$version .
    if ( $push ); then
        docker push $registry/$p:$version
    fi
done

cd $project_root


