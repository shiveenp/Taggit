#!/bin/bash

echo "Building taggit"
## build new
./gradlew stage
echo "Project build done"

echo "Release new version"
./gradlew release
echo "New version released successfully"

echo 'Building docker image'
## set image tags
image="shiveenp/taggit"
#get release tag
gitTag=$(git describe --tags --abbrev=0 | sed 's/^.//')
gitReleaseTag=$image:$gitTag
echo $gitReleaseTag
# get latest tag
latestTag=$image:latest
#build image
docker buildx build --platform linux/amd64,linux/arm64 -t "$gitReleaseTag" -t "$latestTag" --push .
#remove dangling images
docker system prune -f
echo "Docker image build done"
echo "All done!"