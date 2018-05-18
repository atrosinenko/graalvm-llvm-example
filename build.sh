#!/bin/sh

export JAVA_HOME=$(pwd)/../graalvm-1.0.0-rc1

clang -c -O1 -g -emit-llvm -I${JAVA_HOME}/jre/languages/llvm/ lib.c -o lib.bc
clang -c -O1 -g -emit-llvm -DSQLITE_OMIT_LOAD_EXTENSION -DSQLITE_THREADSAFE=0 3rdparty/sqlite-amalgamation-3230100/sqlite3.c -o sqlite3.bc
