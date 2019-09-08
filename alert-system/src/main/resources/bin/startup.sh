#!/usr/bin/env bash

if [ "$1" == "-daemon" ]; then
    nohup java -Xbootclasspath/a:../conf -jar ../lib/alert-system-v1.0.jar >/dev/null  &
else
    java -Xbootclasspath/a:../conf -jar ../lib/alert-system-v1.0.jar
fi