#!/bin/sh

set -e

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_USER_HOME=${MAVEN_USER_HOME:-"$BASE_DIR/.m2"}
MAVEN_REPO_LOCAL=${MAVEN_REPO_LOCAL:-"$MAVEN_USER_HOME/repository"}

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Maven wrapper JAR not found at $WRAPPER_JAR" >&2
  exit 1
fi

if [ -n "$JAVA_HOME" ]; then
  JAVA_EXE="$JAVA_HOME/bin/java"
else
  JAVA_EXE="java"
fi

export MAVEN_USER_HOME
export MAVEN_REPO_LOCAL

exec "$JAVA_EXE" \
  "-Dmaven.user.home=$MAVEN_USER_HOME" \
  "-Dmaven.repo.local=$MAVEN_REPO_LOCAL" \
  -Dmaven.multiModuleProjectDirectory="$BASE_DIR" \
  -classpath "$WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
