/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_rameon_sing_dsp_processors_NativeTimescaleProcessor */

#ifndef _Included_com_rameon_sing_dsp_processors_NativeTimescaleProcessor
#define _Included_com_rameon_sing_dsp_processors_NativeTimescaleProcessor
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_rameon_sing_dsp_processors_NativeTimescaleProcessor
 * Method:    alloc
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_com_rameon_sing_dsp_processors_NativeTimescaleProcessor_alloc
  (JNIEnv *, jobject, jint, jint, jint);

/*
 * Class:     com_rameon_sing_dsp_processors_NativeTimescaleProcessor
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_rameon_sing_dsp_processors_NativeTimescaleProcessor_free
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_rameon_sing_dsp_processors_NativeTimescaleProcessor
 * Method:    processFrame
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_com_rameon_sing_dsp_processors_NativeTimescaleProcessor_processFrame
  (JNIEnv *, jobject, jlong, jfloatArray);

#ifdef __cplusplus
}
#endif
#endif