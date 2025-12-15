#!/bin/bash
#Author: Rohtash Lakra
clear
#set -euo pipefail
#IFS=$'\n\t'
# Source common version function
source "$(dirname "$0")/version.sh"

echo
JAVA_VERSION=21
export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
echo "${JAVA_HOME}"
echo
SNAPSHOT_VERSION=$(buildVersion SNAPSHOT)
RELEASE_VERSION=$(buildVersion)
#java -jar build/libs/account-service-$RELEASE_VERSION.jar
#./gradlew clean build -x test
./gradlew clean bootRun -Pversion=$RELEASE_VERSION
echo
