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
  CURRENT_PID=`cat $DR_PID`
  kill -0 $CURRENT_PID >> /dev/null 2>&1
  PID_STATUS=$?
  if [ $PID_STATUS -eq 0 ]; then
    echo "Server running with PID $CURRENT_PID";
  else
    echo "$CURRENT_PID process not running"
  fi;
  exit $PID_STATUS;
else
  echo "Server not running";
  exit 1;
fi;