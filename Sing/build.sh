#!/bin/sh

OUTDIR=src/com/rameon/sing/opensl
PKG=com.rameon.sing.opensl

rm -rf src/com/rameon/sing/opensl
mkdir -p src/com/rameon/sing/opensl

swig -java -package $PKG -includeall -verbose -outdir $OUTDIR -c++\
 -I/usr/local/include -I/System/Library/Frameworks/JavaVM.framework/Headers\
 -I./jni -o jni/java_interface_wrap.cpp opensl_example_interface.i

ndk-build TARGET_PLATFORM=android-L V=1




