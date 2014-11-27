/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_rameon_sing_dsp_Math */

#ifndef _Included_com_rameon_sing_dsp_Math
#define _Included_com_rameon_sing_dsp_Math
#ifdef __cplusplus
extern "C" {
#endif
#undef com_rameon_sing_dsp_Math_PI
#define com_rameon_sing_dsp_Math_PI 3.1415927f
/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    pow
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_pow
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    log10
 * Signature: (F)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_log10
  (JNIEnv *, jclass, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    min
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_min
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    max
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_max
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    floor
 * Signature: (F)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_floor
  (JNIEnv *, jclass, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    ceil
 * Signature: (F)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_ceil
  (JNIEnv *, jclass, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    sin
 * Signature: (F)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_sin
  (JNIEnv *, jclass, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    cos
 * Signature: (F)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_cos
  (JNIEnv *, jclass, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    sqrt
 * Signature: (F)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_sqrt
  (JNIEnv *, jclass, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    atan2
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_atan2
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    abs
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_abs
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    arg
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_arg
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    real
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_real
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    imag
 * Signature: (FF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_imag
  (JNIEnv *, jclass, jfloat, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    princarg
 * Signature: (F)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_princarg
  (JNIEnv *, jclass, jfloat);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    mean
 * Signature: ([SII)S
 */
JNIEXPORT jshort JNICALL Java_com_rameon_sing_dsp_Math_mean
  (JNIEnv *, jclass, jshortArray, jint, jint);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    rms
 * Signature: ([SII)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_rms___3SII
  (JNIEnv *, jclass, jshortArray, jint, jint);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    rms
 * Signature: ([SIIS)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_rms___3SIIS
  (JNIEnv *, jclass, jshortArray, jint, jint, jshort);

/*
 * Class:     com_rameon_sing_dsp_Math
 * Method:    rms2dbfs
 * Signature: (FFF)F
 */
JNIEXPORT jfloat JNICALL Java_com_rameon_sing_dsp_Math_rms2dbfs
  (JNIEnv *, jclass, jfloat, jfloat, jfloat);

#ifdef __cplusplus
}
#endif
#endif
