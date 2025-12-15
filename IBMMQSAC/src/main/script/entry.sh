#!/bin/bash
# run script as root
SCRIPT="$@"
echo $SCRIPT
sudo su - root --session-command="$SCRIPT"
