#!/bin/sh

OUTDIR=src/com/rameon/sing/opensl
PKG=com.rameon.sing.opensl

rm -rf src/com/rameon/sing/opensl
mkdir -p src/com/rameon/sing/opensl


#jni/GenerateHeaders.sh \
#	"Math" \
#	"com.rameon.sing.dsp" \
#	"com/rameon/sing/dsp" \
#	"Math"
#jni/GenerateHeaders.sh \
#	"KissFFT" \
#	"com.rameon.sing.dsp" \
#	"com/rameon/sing/dsp" \
#	"KissFFT"
#jni/GenerateHeaders.sh \
#	"NativeResampleProcessor" \
#	"com.rameon.sing.dsp.processors" \
#	"com/rameon/sing/dsp/processors" \
#	"Processors"
#jni/GenerateHeaders.sh \
#	"NativeTimescaleProcessor" \
#	"com.rameon.sing.dsp.processors" \
#	"com/rameon/sing/dsp/processors" \
#	"Processors"
#
swig -java -package $PKG -includeall -verbose -outdir $OUTDIR -c++\
 -I/usr/local/include -I/System/Library/Frameworks/JavaVM.framework/Headers\
 -I./jni -o jni/java_interface_wrap.cpp sing_interface.i

ndk-build TARGET_PLATFORM=android-L V=1




