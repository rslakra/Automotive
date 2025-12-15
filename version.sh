#!/bin/bash
# Author: Rohtash Lakra
# Common version function used by build and run scripts

VERSION="0.0"
# Build Version Function
function buildVersion() {
  GIT_COMMIT_COUNT=$(git rev-list HEAD --count)
  if [ $? -ne 0 ]; then
    VERSION="${VERSION}.1"
  else
    VERSION="${VERSION}.${GIT_COMMIT_COUNT}"
  fi
  SNAPSHOT="${SNAPSHOT:-$!}"
  if [[ ! -z ${SNAPSHOT} ]]; then
      VERSION="${VERSION}-SNAPSHOT"
  fi

  echo "${VERSION}";
}

