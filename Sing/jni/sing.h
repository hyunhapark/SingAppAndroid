/*
sing.h:
sing module header, used for SWIG wrapping
Copyright (c) 2014, HyunHa Park
All rights reserved.
*/

#ifndef SING_SING_H
#define SING_SING_H


// This is id constants of instruments.
#define INST_NONE			0
#define INST_AUCU_GUITER	1
#define INST_ELEC_GUITER	2
#define INST_PIANO			3

// This is total number of types of instruments.
#define INST_TYPES			3

//This is number octaves from 0
#define OCTAVE_NUM			8		// C0 ~ C7 available.

#ifdef __cplusplus
extern "C" {
#endif
  void start_base_process();
  void stop_base_process();
  void start_inst_process();
  void stop_inst_process();
  void inst_mute();
  void inst_unmute();
#ifdef __cplusplus
};
#endif

#endif // #ifndef SING_SING_H
