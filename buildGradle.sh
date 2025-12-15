#!/bin/bash
# Author: Rohtash Lakra
clear
# Source common version function
source "$(dirname "$0")/version.sh"

echo
JAVA_VERSION=21
export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
echo "${JAVA_HOME}"
echo
#./gradlew clean build -x test
SNAPSHOT_VERSION=$(buildVersion SNAPSHOT)
RELEASE_VERSION=$(buildVersion)
#echo "RELEASE_VERSION: ${RELEASE_VERSION}, SNAPSHOT_VERSION: ${SNAPSHOT_VERSION}"
#./gradlew clean build -x test -Pversion=$RELEASE_VERSION
./gradlew clean build -x test -Pversion=$SNAPSHOT_VERSION
./gradlew build -x test -Pversion=$RELEASE_VERSION
#./gradlew clean build -x test -Pversion=$(./makeVersion.sh SNAPSHOT)
echo
