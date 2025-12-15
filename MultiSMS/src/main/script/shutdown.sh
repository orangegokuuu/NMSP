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
  CURRENT_PID=`cat $EMG_PID`
  echo "Server running with PID $CURRENT_PID shutdown now";
  curl -u $EMG_ADMIN_USER:$EMG_ADMIN_PASS -X POST http://localhost:$EMG_ADMIN_PORT/$EMG_ADMIN_CONTEXT/shutdown 2>/dev/null
  exit $?;
else
  echo "Server not running";
  exit 1;
fi;
