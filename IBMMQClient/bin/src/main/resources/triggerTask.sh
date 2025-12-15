#!/bin/bash -x
#
#

TRIGGER_PATH="/tmp/trigger.sh"

QMGRS=$(dspmq | sed 's/).*//g' | sed 's/.*(//g' )

for qmgr in $QMGRS
do
    localqueues=$(echo "dis ql(*)" |runmqsc $qmgr | grep -v SYSTEM | grep -o "QUEUE(.*)" | grep -o "SMS.*.REQ.Q" | awk '{print $1}' | cut -d'(' -f2 | cut -d')' -f1)

    for localqueue in $localqueues
    do
        queue_depth_count=$(echo "dis ql($localqueue) curdepth" | runmqsc $qmgr| grep CUR | cut -d'(' -f2  | cut -d')' -f1)

        #DEBUG "Warning : The current depth of local queue $localqueue in $qmgr is $queue_depth_count!!"

        if [ $queue_depth_count -gt 0 ]; then
           cpname=$(echo $(echo $localqueue | cut -d'.' -f2 | cut -d'.' -f1))
           echo "Trigger cp[$cpname]"
           $TRIGGER_PATH $cpname
        fi
    done
done
