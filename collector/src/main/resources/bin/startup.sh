#!/usr/bin/env bash

if [ "$1" == "-daemon" ]; then
    nohup java -Xbootclasspath/a:../conf -jar ../lib/collector-v1.0.jar >/dev/null  &
else
    java -Xbootclasspath/a:../conf -jar ../lib/collector-v1.0.jar
fi