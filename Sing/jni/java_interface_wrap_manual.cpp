#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include "asset_io.h"
#include "Utils.h"

#ifdef __cplusplus
extern "C" {
#endif

void Java_com_rameon_sing_opensl2_AssetLoader_set_1asset_1manager(JNIEnv *env, jclass obj,
		jobject assetManager) {
	LOG("set_asset_manager() inside.",0);

	AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
	if (mgr == NULL) {
		__android_log_print(ANDROID_LOG_ERROR, "Sing",
				"error loading asset manager");
	} else {
		__android_log_print(ANDROID_LOG_VERBOSE, "Sing",
				"loaded asset manager");
		android_fopen_set_asset_manager(mgr);
	}
}

#ifdef __cplusplus
}
#endif
