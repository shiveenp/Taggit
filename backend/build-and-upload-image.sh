#!/bin/bash

## clean build director
./gradlew clean

## build new
./gradlew build -x test

## set image tags
image="shiveenp/taggit"
registry="registry.digitalocean.com/side-projects"

#get timestamp for the tag
timestamp=$(date +%Y%m%d%H%M%S)

tag=$image:$timestamp
latest=$image:latest

#build image
docker build -t "$tag" .

#remove dangling images
docker system prune -f

#push to DO registry
doctl registry login
docker tag "$tag" $registry/"$tag"
docker push $registry/"$tag"
