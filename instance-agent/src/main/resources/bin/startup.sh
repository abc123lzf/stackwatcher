#!/usr/bin/env bash

if [ ! $JAVA_HOME ]; then
    echo "JAVA_HOME not defined"
    exit 1
fi

cp -r ../sigar/lib/. $JAVA_HOME/bin

if [ "$1" == "-daemon" ]; then
    nohup java -Xbootclasspath/a:../conf -jar ../lib/instance-agent-v1.0.jar >/dev/null  &
else
    java -Xbootclasspath/a:../conf -jar ../lib/instance-v1.0.jar
fi