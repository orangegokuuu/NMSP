#!/bin/bash

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

. $PRGDIR/ctl.sh
. $PRGDIR/env.sh

if [[ -f $DR_PID ]]; then
  CURRENT_PID=`cat $API_PID`
  echo "Server running with PID $CURRENT_PID shutdown now";
  curl -u $API_ADMIN_USER:$API_ADMIN_PASS -X POST http://localhost:$API_ADMIN_PORT/$API_ADMIN_CONTEXT/shutdown 2>/dev/null
  exit $?;
else
  echo "Server not running";
  exit 1;
fi;
