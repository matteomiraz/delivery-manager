#!/bin/bash

nohup java -jar broker.jar $1 > broker.out 2> broker.err & 
echo $! > broker.pid

echo "Started broker with pid `cat broker.pid`"