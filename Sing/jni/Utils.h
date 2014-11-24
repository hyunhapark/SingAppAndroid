/*
 Utils.c:
 utils header
 Copyright (c) 2014, HyunHa Park
 All rights reserved.
 */

#ifndef SING_UTILS_H
#define SING_UTILS_H

#define LOGCAT_TAG "SingApp"
#define LOGGING 1

#if LOGGING
	#include <android/log.h>
	#define LOG(message, args...) __android_log_print(ANDROID_LOG_DEBUG, LOGCAT_TAG, message, args)
#else
	#define LOG(message, args...) while(0){}
#endif

#endif //#ifndef SING_UTILS_H
