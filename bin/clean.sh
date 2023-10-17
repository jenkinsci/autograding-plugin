#!/bin/bash

set -e

(cd plugin; mvn clean install -Djenkins.test.timeout=1000 || { echo "Build failed"; exit 1; })

$(dirname "$0")/deploy.sh autograding


