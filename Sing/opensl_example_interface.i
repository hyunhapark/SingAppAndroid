/*
opensl_example_interface.i:
SWIG interface file to sing functions
Copyright (c) 2014, HyunHa Park
All rights reserved.
*/

%module SingModule
%{
#include "sing.h"
%}

/*
// Enable the JNI class to load the required native library.
%pragma(java) jniclasscode=%{
  static {
    try {
        java.lang.System.loadLibrary("Sing");
    } catch (UnsatisfiedLinkError e) {
        java.lang.System.err.println("native code library failed to load.\n" + e);
        java.lang.System.exit(1);
    }
  }
%}
*/

%include "sing.h"
