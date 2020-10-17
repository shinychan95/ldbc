#!/bin/bash

# exit upon non-zero pipe (roughly: command) status
set -e

QUERY_NUMBER=$1
ACTION=${2:-'run'} # supply jar to generate JAR file
QUERY_SET=${3:-'bi'} # give the query set name, defaults to bi
DBTOASTER_BIN=${DBTOASTER_BIN:-$HOME/bin/dbtoaster/bin/dbtoaster}

if [ "${QUERY_NUMBER}x" == "x" ]; then
  echo 1>&2 "ERROR: No query number was provided. Provide ${QUERY_SET} query number as first argument, e.g. '$0 1'"
  exit -2
fi

TMP_SQL_SCRIPT=$(mktemp)

cat >$TMP_SQL_SCRIPT <<HERE
INCLUDE 'schema.sql';

INCLUDE 'queries/${QUERY_SET}-${QUERY_NUMBER}.sql';
HERE

if   [ "${ACTION}x" == "runx" ]; then
  ${DBTOASTER_BIN} -l scala -r $TMP_SQL_SCRIPT
elif [ "${ACTION}x" == "jarx" ]; then
  ${DBTOASTER_BIN} -l scala -c target/ldbc-${QUERY_SET}-q${QUERY_NUMBER}.jar -n ${QUERY_SET}Q${QUERY_NUMBER} $TMP_SQL_SCRIPT
else
  echo "ERROR: invalid action encountered: ${ACTION} Use run (this is the default, when omitted) or jar to run the query or generate jar, respectively."
fi

# clean up temp script
rm $TMP_SQL_SCRIPT
