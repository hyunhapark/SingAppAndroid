#!/bin/sh

rm -rf src/com/rameon/sing/opensl
mkdir -p src/com/rameon/sing/opensl

swig -java -package com.rameon.sing.opensl -includeall -verbose -outdir src/com/rameon/sing/opensl -c++ -I/usr/local/include -I/System/Library/Frameworks/JavaVM.framework/Headers -I./jni -o jni/java_interface_wrap.cpp opensl_example_interface.i

ndk-build TARGET_PLATFORM=android-L V=1




