#!/bin/bash

set -e

(cd plugin; mvn clean install -Pskip || { echo "Build failed"; exit 1; })

$(dirname "$0")/deploy.sh autograding

