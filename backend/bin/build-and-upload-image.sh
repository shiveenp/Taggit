#!/bin/bash

echo "Building taggit"
## build new
./gradlew clean build -x test
echo "Project build done"

echo "Login to registry"
doctl registry login
echo "Registry logged in successfully"

echo 'Building docker image'
## set image tags
image="shiveenp/taggit"
registry="registry.digitalocean.com/side-projects"
#get timestamp for the tag
timestamp=$(date +%Y%m%d%H%M%S)
tag=$image:$timestamp
registryTag=$registry/"$tag"
latest=$image:latest
#build image
docker buildx build --platform linux/amd64,linux/arm64 -t "$registryTag" --push .
#remove dangling images
docker system prune -f
echo "Docker image build done"
echo "All done!"