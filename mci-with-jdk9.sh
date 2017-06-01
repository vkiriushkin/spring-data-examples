#!/bin/sh
MAVEN_OPTS='--add-modules java.se.ee' mvn clean install -Pjdk9 "$@"
