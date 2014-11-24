/*
sing.h:
sing module header, used for SWIG wrapping
Copyright (c) 2014, HyunHa Park
All rights reserved.
*/

#ifndef SING_SING_H
#define SING_SING_H

#ifdef __cplusplus
extern "C" {
#endif
  void start_base_process();
  void stop_base_process();
  void start_inst_process();
  void stop_inst_process();
#ifdef __cplusplus
};
#endif

#endif // #ifndef SING_SING_H
