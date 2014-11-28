//#define LOG_TAG "simplejni native.cpp"
#include <stdio.h>
#include "jni.h"
#define JNI_VERSION_1_1 0x00010001
#define JNI_VERSION_1_2 0x00010002
#define JNI_VERSION_1_4 0x00010004
#define JNI_VERSION_1_6 0x00010006

#include "Utils.h"

JNIEXPORT jint JNICALL JNI_OnLoad( JavaVM *vm, void *pvt ) {
  LOG("OnLoad",0);
  return JNI_VERSION_1_6;
}
