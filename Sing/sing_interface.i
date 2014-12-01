/*
sing_interface.i:
SWIG interface file to sing functions
Copyright (c) 2014, HyunHa Park
All rights reserved.
*/

%module SingModule
%{
#include "sing.h"
#include "asset_io.h"
%}


// Enable the JNI class to load the required native library.
%pragma(java) jniclasscode=%{
  //public final static native void set_asset_manager(Object assetManager);
%}
/*
%inline %{
extern void android_fopen_set_asset_manager(jobject assetManager);
%}
*/
%include "sing.h"


