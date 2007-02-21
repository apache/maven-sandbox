#!/bin/sh

PROJECT_KEY=$1
PROJECT_VERSION=$2

sed -e "s/PROJECT_KEY/$PROJECT_KEY/;s/PROJECT_VERSION/$PROJECT_VERSION/" plugin-vote.vm > tmp

java -jar swizzle-jirareport-1.2.1-SNAPSHOT-dep.jar tmp
          
