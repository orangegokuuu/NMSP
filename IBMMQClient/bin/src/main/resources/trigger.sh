#!/bin/bash
targetHost="192.168.9.152:9096"

curl -X POST http://$targetHost/mqclient/triggerQueue/$1/ > /dev/null &
