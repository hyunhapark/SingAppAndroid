/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_rameon_sing_dsp_KissFFT */

#ifndef _Included_com_rameon_sing_dsp_KissFFT
#define _Included_com_rameon_sing_dsp_KissFFT
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_rameon_sing_dsp_KissFFT
 * Method:    alloc
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_com_rameon_sing_dsp_KissFFT_alloc
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_rameon_sing_dsp_KissFFT
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_rameon_sing_dsp_KissFFT_free
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_rameon_sing_dsp_KissFFT
 * Method:    fft
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_com_rameon_sing_dsp_KissFFT_fft
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     com_rameon_sing_dsp_KissFFT
 * Method:    ifft
 * Signature: (J[F)V
 */
JNIEXPORT void JNICALL Java_com_rameon_sing_dsp_KissFFT_ifft
  (JNIEnv *, jobject, jlong, jfloatArray);

#ifdef __cplusplus
}
#endif
#endif
