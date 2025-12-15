#!/bin/bash
cpname=$1
user=`echo $cpname | tr '/A-Z/' '/a-z/'`

# delete user.
sudo userdel -r "$user"


