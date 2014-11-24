/*
 sing.c:
 sing main module
 Copyright (c) 2014, HyunHa Park
 All rights reserved.
 */

#include <android/log.h>
#include <stdio.h>
#include <math.h>
#include "opensl_io.h"

#define APPNAME "SingApp"

#define BUFFERFRAMES		16384
#define VECSAMPS_MONO		8192
#define VECSAMPS_STEREO		16384
#define SR 					44100
#define WINDOW_ITV			2048

#define INST_BUFFERFRAMES 		16384
#define INST_VECSAMPS_MONO 		8192
#define INST_VECSAMPS_STEREO 	16384
#define EXTEND_FACTOR			60

static int b_on, i_on;
void start_base_process() {
	OPENSL_STREAM *p;
	int samps, i, j;
	float inbuffer[VECSAMPS_MONO], outbuffer[VECSAMPS_STEREO];
	p = android_OpenAudioDevice(SR, 1, 2, BUFFERFRAMES);
	if (p == NULL)
		return;
	b_on = 1;
	while (b_on) {
		samps = android_AudioIn(p, inbuffer, VECSAMPS_MONO);
		for (i = 0, j = 0; i < samps; i++, j += 2)
			outbuffer[j] = outbuffer[j + 1] = inbuffer[i];
		android_AudioOut(p, outbuffer, samps * 2);
	}
	android_CloseAudioDevice(p);
}

void stop_base_process() {
	b_on = 0;
}

void start_inst_process() {

	OPENSL_STREAM *p;
	int samps, i, j, max_i = 0;
	float max = 0;
	float inbuffer[INST_VECSAMPS_MONO], midbuffer[2 * INST_VECSAMPS_MONO + 1],
			mid2buffer[EXTEND_FACTOR * 2 * INST_VECSAMPS_MONO + 1];

	//FILE *fp = fopen("/sdcard/a.csv","a+");

	p = android_OpenAudioDevice(SR, 1, 2, INST_BUFFERFRAMES);
	if (p == NULL)
		return;

	FILE *fp;
	int fpq_i = 0;

	i_on = 1;
	while (i_on) {
		max = 0;
		max_i = 0;

		char s_i[2] = { '0' + fpq_i, 0 };
		fp = fopen(strcat(strcat(strcpy(malloc(128), "/sdcard/"), s_i), ".csv"),
				"w");

		// Get from mic.
		samps = android_AudioIn(p, inbuffer, INST_VECSAMPS_MONO);

		for (i = 0; i < samps; i++) {
			midbuffer[2 * i + 1] = inbuffer[i];
			midbuffer[2 * i + 2] = 0; // Fill imaginary part with zero
		}

		// Fast Fourier Transform. Changing from time domain to frequency domain.
		four1(midbuffer, INST_VECSAMPS_MONO, 1);

		for (i = 1; i < (INST_VECSAMPS_MONO / 2); i++) {
			fprintf(fp, "%f,%f\n", SR * ((double) i) / INST_VECSAMPS_MONO,
					midbuffer[2 * i + 1]);
		}

		fclose(fp);
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%d.csv saved.",
				fpq_i);
		fpq_i = (fpq_i + 1) % 10;

		// Get estimated frequency using HPS(Harmonic Product Spectrum)
		/*for(i = 1 ; i < (INST_VECSAMPS_MONO/2/5-1)*EXTEND_FACTOR ; i++){
		 // Harmonic Product Spectrum
		 float elem[5];
		 elem[0] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[2*(i/EXTEND_FACTOR)+1] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[2*i/EXTEND_FACTOR+3];
		 switch ((i%EXTEND_FACTOR) / (EXTEND_FACTOR/2)) {
		 case 0: elem[1] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[4*(i/EXTEND_FACTOR)+1] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[4*i/EXTEND_FACTOR+3]; break;
		 case 1: elem[1] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[4*(i/EXTEND_FACTOR)+3] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[4*i/EXTEND_FACTOR+5]; break;
		 }
		 switch ((i%EXTEND_FACTOR) / (EXTEND_FACTOR/3)) {
		 case 0: elem[2] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[6*(i/EXTEND_FACTOR)+1] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[6*i/EXTEND_FACTOR+3]; break;
		 case 1: elem[2] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[6*(i/EXTEND_FACTOR)+3] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[6*i/EXTEND_FACTOR+5]; break;
		 case 2: elem[2] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[6*(i/EXTEND_FACTOR)+5] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[6*i/EXTEND_FACTOR+7]; break;
		 }
		 switch ((i%EXTEND_FACTOR) / (EXTEND_FACTOR/4)) {
		 case 0: elem[3] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[8*(i/EXTEND_FACTOR)+1] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[8*i/EXTEND_FACTOR+3]; break;
		 case 1: elem[3] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[8*(i/EXTEND_FACTOR)+3] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[8*i/EXTEND_FACTOR+5]; break;
		 case 2: elem[3] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[8*(i/EXTEND_FACTOR)+5] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[8*i/EXTEND_FACTOR+7]; break;
		 case 3: elem[3] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[8*(i/EXTEND_FACTOR)+7] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[8*i/EXTEND_FACTOR+9]; break;
		 }
		 switch ((i%EXTEND_FACTOR) / (EXTEND_FACTOR/5)) {
		 case 0: elem[4] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[10*(i/EXTEND_FACTOR)+1] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[10*i/EXTEND_FACTOR+3]; break;
		 case 1: elem[4] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[10*(i/EXTEND_FACTOR)+3] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[10*i/EXTEND_FACTOR+5]; break;
		 case 2: elem[4] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[10*(i/EXTEND_FACTOR)+5] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[10*i/EXTEND_FACTOR+7]; break;
		 case 3: elem[4] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[10*(i/EXTEND_FACTOR)+7] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[10*i/EXTEND_FACTOR+9]; break;
		 case 4: elem[4] = (1-(i%EXTEND_FACTOR)/EXTEND_FACTOR)*midbuffer[10*(i/EXTEND_FACTOR)+9] + (i%EXTEND_FACTOR)/EXTEND_FACTOR*midbuffer[10*i/EXTEND_FACTOR+11]; break;
		 }
		 float tmp = elem[0] * elem[1] * elem[2] * elem[3] * elem[4];


		 tmp = tmp>0 ? tmp : -tmp; // get absolute value of tmp

		 if(max < tmp){		// find max amplitude and it's index(frequency)
		 max = tmp;
		 max_i = i;
		 }
		 //__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%f,%f", SR*((double)i/EXTEND_FACTOR)/INST_VECSAMPS_MONO, tmp);
		 //fprintf(fp, "%f,%f\n", SR*((double)i/EXTEND_FACTOR)/INST_VECSAMPS_MONO, tmp);
		 }*/

		/*char * code;
		 double f = SR*((double)max_i/EXTEND_FACTOR)/INST_VECSAMPS_MONO;			// real frequency
		 switch ( (int)(12*(log(f/440.0) / log(2))+120) % 12){
		 case 0:  code = "A"; break;
		 case 1:  code = "A#"; break;
		 case 2:  code = "B"; break;
		 case 3:  code = "C"; break;
		 case 4:  code = "C#"; break;
		 case 5:  code = "D"; break;
		 case 6:  code = "D#"; break;
		 case 7:  code = "E"; break;
		 case 8:  code = "F"; break;
		 case 9:  code = "F#"; break;
		 case 10: code = "G"; break;
		 case 11: code = "G#"; break;
		 }

		 // Cut off noise
		 if(max > 100.0){
		 __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "f:%fHz (code:%s) (i:%d -> %f)\n",f, code, max_i, max);
		 }*/
	}
	//fclose(fp);
	android_CloseAudioDevice(p);
}

void stop_inst_process() {
	i_on = 0;
}
