#!/bin/sh

OUTDIR=src/com/rameon/sing/opensl
PKG=com.rameon.sing.opensl

rm -rf src/com/rameon/sing/opensl
mkdir -p src/com/rameon/sing/opensl


jni/GenerateHeaders.sh \
	"Math" \
	"de.jurihock.voicesmith.dsp" \
	"de/jurihock/voicesmith/dsp" \
	"Math"
jni/GenerateHeaders.sh \
	"KissFFT" \
	"de.jurihock.voicesmith.dsp" \
	"de/jurihock/voicesmith/dsp" \
	"KissFFT"
jni/GenerateHeaders.sh \
	"NativeResampleProcessor" \
	"de.jurihock.voicesmith.dsp.processors" \
	"de/jurihock/voicesmith/dsp/processors" \
	"Processors"
jni/GenerateHeaders.sh \
	"NativeTimescaleProcessor" \
	"de.jurihock.voicesmith.dsp.processors" \
	"de/jurihock/voicesmith/dsp/processors" \
	"Processors"

swig -java -package $PKG -includeall -verbose -outdir $OUTDIR -c++\
 -I/usr/local/include -I/System/Library/Frameworks/JavaVM.framework/Headers\
 -I./jni -o jni/java_interface_wrap.cpp opensl_example_interface.i

ndk-build TARGET_PLATFORM=android-L V=1




