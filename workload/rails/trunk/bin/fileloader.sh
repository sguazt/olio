#!/bin/sh
#Script to run loader by hand

if [ -z "$1" ] ; then
    echo "Usage: $0 [concurrent users]" >&2
    exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
    echo "Please set JAVA_HOME and restart command" >&2
    exit 1
fi

SCALE=$1

BINDIR=`dirname $0`

# This script is in $FABAN_HOME/benchmarks/Web20Driver/bin
# we need to go up 4 levels to get to $FABAN_HOME.
if [ -n "$BINDIR" ]
then
    #FABAN_HOME=`cd $BINDIR/../../.. > /dev/null 2>&1 && pwd`
    BENCH_HOME=`cd $BINDIR/.. > /dev/null 2>&1 &&pwd`
    export FABAN_HOME BENCH_HOME
fi

B=$BENCH_HOME/lib
L=$FABAN_HOME/lib
CLASSPATH=$B/Web20Driver.jar:$L/commons-httpclient-2.0.1.jar:\
$L/fabancommon.jar:$L/commons-logging.jar:$L/fabandriver.jar:$L/fabanagents.jar
export CLASSPATH

$JAVA_HOME/bin/java -server com.sun.web20.fsloader.FileLoader \
    $BENCH_HOME/resources $SCALE

EXIT_CODE=$?
if [ "$EXIT_CODE" = 0 ] ; then
    echo "File Load Successful"
else
    echo "ERROR: File loader exited with code ${EXIT_CODE}."
fi