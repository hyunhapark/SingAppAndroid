/*
 sing.c:
 sing main module
 Copyright (c) 2014, HyunHa Park
 All rights reserved.
 */

#include <stdio.h>
#include <math.h>
#include "opensl_io.h"
#include "Utils.h"


#define ON_FILE_DEBUGGING		1

#define BUFFERFRAMES			16384
#define VECSAMPS_MONO			8192
#define VECSAMPS_STEREO			16384	//(VECSAMPS_MONO*2)
#define SR 						44100
#define FRAME_ITV				2048	//(VECSAMPS_MONO/4)
#define PASS_FREQ_MIN			(int)(70./SR*VECSAMPS_MONO)
#define PASS_FREQ_MAX			(int)(4000./SR*VECSAMPS_MONO)

#define MIN(a,b)				(a<b?a:b)
#define MAX(a,b)				(a>b?a:b)
#define ABS(a)					(a<0?-a:a)




static float inst_wave_chunck[INST_TYPES][OCTAVE_NUM][VECSAMPS_MONO*2];

static int b_on = 0;	// base_process running?
static int i_on = 0;	// inst_process running?


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
	float max = 0., a = 0.;
	float inbuffer[FRAME_ITV], midbuffer[2 * VECSAMPS_MONO + 1], outbuffer[VECSAMPS_STEREO];
	//float mid2buffer[2 * VECSAMPS_MONO + 1];
	float **frame = (float **) calloc(sizeof(float *), 4);	//float frame[4][VECSAMPS_MONO];
	for(i = 0 ; i<4 ; i++)
		frame[i] = (float *) calloc(sizeof(float), VECSAMPS_MONO);

	p = android_OpenAudioDevice(SR, 1, 2, BUFFERFRAMES);
	if (p == NULL)
		return;

#if ON_FILE_DEBUGGING
	FILE *fp;
	int fpq_i = 0;
	int fpq_i2 = 0;
#endif

	// Get from mic.
	samps = android_AudioIn(p, inbuffer + FRAME_ITV, VECSAMPS_MONO-FRAME_ITV);

	i_on = 1;
	while (i_on) {

		max = 0.;
		max_i = 0;

#if ON_FILE_DEBUGGING
		char s_i[2] = { '0' + fpq_i, 0 };
		char f_name[128]="/sdcard/";
		fp = fopen(strcat(strcat(f_name, s_i), ".csv"), "w");
#endif

		// Get from mic.
		samps += android_AudioIn(p, inbuffer + VECSAMPS_MONO - FRAME_ITV, FRAME_ITV);

		for (i = 0; i < samps; i++) {
			midbuffer[2 * i + 1] = inbuffer[i];
			midbuffer[2 * i + 2] = 0; // Fill imaginary part with zero
		}

		// Fast Fourier Transform. Changing from time domain to frequency domain.
		four1(midbuffer, VECSAMPS_MONO, 1);

		for (i = MAX(PASS_FREQ_MIN, 1) ; i < MIN(PASS_FREQ_MAX, VECSAMPS_MONO/2) ; i++) {
#if ON_FILE_DEBUGGING
			fprintf(fp, "%f,%f\n", SR * (float) i / VECSAMPS_MONO, midbuffer[2 * i + 1]);
#endif
			if(max < tmp){		// find max amplitude and it's index(frequency)
				max = tmp;
				max_i = i;
			}
		}

#if ON_FILE_DEBUGGING
		fclose(fp);
		fp=null;

		LOG("%d.csv saved.", fpq_i);
		fpq_i = (fpq_i + 1) % 10;
#endif

		char * code;
		float f = SR * (float) max_i / VECSAMPS_MONO; // real frequency
		switch ((int) (12 * (log(f / 440.0) / log(2)) + 120) % 12) {
		case 0: code = "A"; break;
		case 1: code = "A#"; break;
		case 2: code = "B"; break;
		case 3: code = "C"; break;
		case 4: code = "C#"; break;
		case 5: code = "D"; break;
		case 6: code = "D#"; break;
		case 7: code = "E"; break;
		case 8: code = "F"; break;
		case 9: code = "F#"; break;
		case 10: code = "G"; break;
		case 11: code = "G#"; break;
		}

		a = max;
		// Cut off noise(low amplitude)
		if (max > 100.0) {
			LOG("[ORI] f:%fHz (code:%s) (i:%d -> %f)\n", f, code, max_i, max);
		}

		max = 0.;
		max_i = 0;

#if ON_FILE_DEBUGGING
		char s_i2[2] = { '0' + fpq_i2, 0 };
		char f_name2[128]="/sdcard/HPS-";
		fp = fopen(strcat(strcat(f_name2, s_i2), ".csv"), "w");
#endif

		// Get estimated frequency using HPS(Harmonic Product Spectrum)
		for (i = MAX(PASS_FREQ_MIN, 1) ; i < MIN(PASS_FREQ_MAX, VECSAMPS_MONO/2) ; i++) {
			// Harmonic Product Spectrum
			float tmp = midbuffer[2*i+1] * midbuffer[4*i+1] * midbuffer[6*i+1] * midbuffer[8*i+1] * midbuffer[10*i+1];

			tmp = ABS(tmp); // get absolute value of tmp

			// Get estimated frequency using HPS(Harmonic Product Spectrum)
			if(max < tmp){		// find max amplitude and it's index(frequency)
				max = tmp;
				max_i = i;
			}
#if ON_FILE_DEBUGGING
			fprintf(fp, "%f,%f\n", SR * (float)i / VECSAMPS_MONO, tmp);
#endif
		}

#if ON_FILE_DEBUGGING
		fclose(fp);
		fp=null;

		LOG("HPS-%d.csv saved.", fpq_i2);
		fpq_i2 = (fpq_i2 + 1) % 10;
#endif

		f = SR * (float) max_i / VECSAMPS_MONO; // real frequency
		switch ((int) (12 * (log(f / 440.0) / log(2)) + 120) % 12) {
		case 0: code = "A"; break;
		case 1: code = "A#"; break;
		case 2: code = "B"; break;
		case 3: code = "C"; break;
		case 4: code = "C#"; break;
		case 5: code = "D"; break;
		case 6: code = "D#"; break;
		case 7: code = "E"; break;
		case 8: code = "F"; break;
		case 9: code = "F#"; break;
		case 10: code = "G"; break;
		case 11: code = "G#"; break;
		}

		// Cut off noise(low amplitude)
		if (a > 100.0) {
			LOG("[HPS] f:%fHz (code:%s) (i:%d -> %f)\n", f, code, max_i, max);
		}

	}
	android_CloseAudioDevice(p);
}

void stop_inst_process() {
	i_on = 0;
}



// Loads wave data chuck of instruments to inst_wave;
int inst_load(){
	// ex. Where to load Piano C1 data chuck
	//   =>  inst_wave_chunck[INST_PIANO][1][0]
	//          ~ inst_wave_chunck[INST_PIANO][1][0VECSAMPS_MONO*2]

	// TODO : load

}


// Called only when amplitude is higher than low-amp cut off threshold
float *get_inst_frame(int id, float f) {
	if (id==INST_NONE){
		return;
	}

	float *current_frame = (float *) malloc(sizeof(float)*VECSAMPS_MONO);

	// TODO : get frame of size VECSAMPS_MONO by linear interpolation


	// TODO : multiply by hanning window


	// return the frame
	return current_frame;
}

