LOCAL_PATH := $(call my-dir)



# lib1 (libSing) start
include $(CLEAR_VARS)

# Name of the library without prefix "lib" and file extension
LOCAL_MODULE   := Sing
LOCAL_C_INCLUDES := $(LOCAL_PATH)
LOCAL_CFLAGS := -Wall -O3 -ffast-math -funroll-loops -fomit-frame-pointer -I/usr/lib/jvm/java-6-openjdk-amd64/include -I/usr/lib/jvm/java-6-openjdk-amd64/include/linux
LOCAL_CPPFLAGS :=$(LOCAL_CFLAGS)
###

LOCAL_SRC_FILES := sing.c \
opensl_io.c \
fft.c \
java_interface_wrap.cpp \
native.cpp
#asset_io.c

LOCAL_LDLIBS := -llog -lOpenSLES
LOCAL_SHARED_LIBRARIES += libandroid

include $(BUILD_SHARED_LIBRARY)
# lib1 end




# lib2 (libVoicesmith) start
include $(CLEAR_VARS)

# Name of the library without prefix "lib" and file extension
LOCAL_MODULE := Voicesmith

# Optimization flags (see KissFFT makefile)
LOCAL_ARM_MODE := arm
LOCAL_CFLAGS := -Wall -O3 -ffast-math -funroll-loops -fomit-frame-pointer

# LogCat support
LOCAL_LDLIBS := -llog

# Debugging flag
LOCAL_CFLAGS += -g

# Include all .c/.cpp files to build
LOCAL_SRC_FILES := $(shell cd $(LOCAL_PATH); \
	find KissFFT/src -type f -name '*.c'; \
	find KissFFT -type f -name '*.cpp'; \
	find Math -type f -name '*.cpp'; \
	find Processors -type f -name '*.cpp')

include $(BUILD_SHARED_LIBRARY)
# lib2 end