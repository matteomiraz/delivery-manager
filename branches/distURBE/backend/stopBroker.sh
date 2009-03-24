#!/bin/bash

if [ -f broker.pid ];
then
    BROKER=`cat broker.pid`
    echo "Shutting down broker with PID $BROKER"
    kill $BROKER
    rm broker.pid
fi