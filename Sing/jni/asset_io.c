#include <stdio.h>

#include "asset_io.h"
#include <errno.h>
#include <android/asset_manager.h>
#include "Utils.h"

static int android_read(void* cookie, char* buf, int size) {
  return AAsset_read((AAsset*)cookie, buf, size);
}

static int android_write(void* cookie, const char* buf, int size) {
  return EACCES; // can't provide write access to the apk
}

static fpos_t android_seek(void* cookie, fpos_t offset, int whence) {
  return AAsset_seek((AAsset*)cookie, offset, whence);
}

static int android_close(void* cookie) {
  AAsset_close((AAsset*)cookie);
  return 0;
}

// must be established by someone else...
AAssetManager* android_asset_manager;
void android_fopen_set_asset_manager(AAssetManager *manager) {
  android_asset_manager = manager;
}

FILE* android_fopen(const char* fname, const char* mode) {
//  LOG("%s() %s,%s (L%d)", __FUNCTION__, fname, mode, __LINE__);
  if(mode[0] == 'w') return NULL;

//  LOG("%s() opening the asset (L%d)", __FUNCTION__,  __LINE__);
  AAsset* asset = AAssetManager_open(android_asset_manager, fname, 0);
  if(!asset) return NULL;

//  LOG("%s() got asset : 0x%X (L%d)", __FUNCTION__, asset, __LINE__);

  return funopen(asset, android_read, android_write, android_seek, android_close);
}
