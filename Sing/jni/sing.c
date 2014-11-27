/*
 sing.c:
 sing main module
 Copyright (c) 2014, HyunHa Park
 All rights reserved.
 */

#include <stdio.h>
#include <math.h>
#include <string.h>

#include <fcntl.h>
#include <stdio.h>

#include "opensl_io.h"
#include "Utils.h"
#include "sing.h"



#define ON_FILE_DEBUGGING		0

#define SR 						44100
#define BUFFERFRAMES			16384
#define VECSAMPS_MONO			8192
#define VECSAMPS_STEREO			16384	//(VECSAMPS_MONO*2)
#define FRAME_ITV				2048	//(VECSAMPS_MONO/4)
//#define BUFFERFRAMES			32768
//#define VECSAMPS_MONO			16384
//#define VECSAMPS_STEREO			32768	//(VECSAMPS_MONO*2)
//#define FRAME_ITV				4096	//(VECSAMPS_MONO/4)
#define PASS_FREQ_MIN			(int)(70./SR*VECSAMPS_MONO)
#define PASS_FREQ_MAX			(int)(4000./SR*VECSAMPS_MONO)
//#define FFT_DECAY_LEN			4		// should not be bigger than 16

#define MIN(a,b)				(a<b?a:b)
#define MAX(a,b)				(a>b?a:b)
#define ABS(a)					(a<0?-a:a)




static float inst_wave_chunck[INST_TYPES][OCTAVE_NUM][VECSAMPS_MONO*2];

static int b_on = 0;	// base_process running?
static int i_on = 0;	// inst_process running?
static int i_mute = 0;	// inst play mute?

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

	OPENSL_STREAM *p = 0;
	int samps, i, j, max_i = 0;
	int warm_up_end = 0;				// set to 1 when warm up is completed. (7 itv)
	float max = 0., a = 0.;

	float inbuffer[4][FRAME_ITV];
	int inbuffer_i=0;					// circular index of inbuffer
	float fft[4][2 * VECSAMPS_MONO + 1], fft_max_amp[4], fft_aged_sum[2 * VECSAMPS_MONO + 1]={0};
	int fft_i = 0;						// circular index of fft
	float inst_frame[4][VECSAMPS_MONO];
	int inst_frame_i = 0;				// circular index of inst_frame
	float outbuffer[VECSAMPS_STEREO];

	int curr_inst = INST_NONE;


	p = android_OpenAudioDevice(SR, 1, 2, BUFFERFRAMES);
	if (p == NULL)
		return;

#if ON_FILE_DEBUGGING
	FILE *fp;
	int fpq_i = 0;
	int fpq_i2 = 0;
#endif

	// Get from mic.
	samps = android_AudioIn(p, inbuffer[inbuffer_i], 3 * FRAME_ITV);
	inbuffer_i = (inbuffer_i+3) % 4;

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
		samps += android_AudioIn(p, inbuffer[inbuffer_i], FRAME_ITV);
		inbuffer_i = (inbuffer_i+1) % 4;		// circular increase

		for (i = 0; i < VECSAMPS_MONO; i++) {
			switch (i/FRAME_ITV){
			case 0: fft[fft_i][2 * i + 1] = inbuffer[inbuffer_i][i%FRAME_ITV]; break;
			case 1: fft[fft_i][2 * i + 1] = inbuffer[(inbuffer_i+1)%4][i%FRAME_ITV]; break;
			case 2: fft[fft_i][2 * i + 1] = inbuffer[(inbuffer_i+2)%4][i%FRAME_ITV]; break;
			case 3: fft[fft_i][2 * i + 1] = inbuffer[(inbuffer_i+3)%4][i%FRAME_ITV]; break;
			}
			fft[fft_i][2 * i + 2] = 0; // Fill imaginary part with zero
		}

		// Fast Fourier Transform. Changing from time domain to frequency domain.
		four1(fft[fft_i], VECSAMPS_MONO, 1);

		// Get max amplitude of fft for normalize
		fft_max_amp[fft_i] = 1;
		for (i = 1; i < VECSAMPS_MONO/2; i++){
			//TODO
			fft[fft_i][2*i+1] = sqrt(fft[fft_i][2*i+1]*fft[fft_i][2*i+1] + fft[fft_i][2*i+2]*fft[fft_i][2*i+2]);
			fft_max_amp[fft_i] = MAX(fft[fft_i][2*i+1], fft_max_amp[fft_i]);
		}

// temporary debug
#if ON_FILE_DEBUGGING
		for (i=PASS_FREQ_MIN ; i<PASS_FREQ_MAX ; i++){
			fprintf(fp, "%f,%f\n", SR * (float) i / VECSAMPS_MONO, fft[fft_i][2 * i + 1]);
		}
#endif

		fft_i = (fft_i+1) % 4;					// circular increase

		if(!warm_up_end){
			if(fft_i!=0){
				continue;
			}else{
				warm_up_end=1;
			}
		}

		// Normalize each four fft frames and take average of them. (Max:100)
		for (i = 0 ; i < VECSAMPS_MONO/2 ; i++) {
			fft[fft_i][2*i+1]=0;
			for (j=0 ; j<4 ; j++) {
				fft[fft_i][2*i+1] += fft[(fft_i+j)%4][2*i+1]/fft_max_amp[(fft_i+j)%4];
			}
			fft[fft_i][2*i+1] *= 100/4;
			fft_aged_sum[2*i+1] = fft_aged_sum[2*i+1]/2 + fft[fft_i][2*i+1];
			fft[fft_i][2*i+1] = fft_aged_sum[2*i+1];
		}


		for (i = MAX(PASS_FREQ_MIN, 1) ; i < MIN(PASS_FREQ_MAX, VECSAMPS_MONO/2) ; i++) {
#if ON_FILE_DEBUGGING
//			fprintf(fp, "%f,%f\n", SR * (float) i / VECSAMPS_MONO, fft[fft_i][2 * i + 1]);
#endif

			if(max < fft[fft_i][2 * i + 1]){		// find max amplitude and it's index(frequency)
				max = fft[fft_i][2 * i + 1];
				max_i = i;
			}
		}

#if ON_FILE_DEBUGGING
		fclose(fp);
		fp=NULL;

		//LOG("%d.csv saved.", fpq_i);
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

		// Cut off noise(low amplitude)
		if (fft_max_amp[fft_i] > 20.0) {
//			LOG("[ORI] f:%fHz (code:%s) (i:%d -> %f)\n", f, code, max_i, fft_max_amp[fft_i]);
		}

		max = 0.;
		max_i = 0;

#if ON_FILE_DEBUGGING
		char s_i2[2] = { '0' + fpq_i2, 0 };
		char f_name2[128]="/sdcard/HPS-";
		fp = fopen(strcat(strcat(f_name2, s_i2), ".csv"), "w");
#endif




		// Get estimated frequency using HPS(Harmonic Product Spectrum)
		for (i = MAX(PASS_FREQ_MIN, 1) ; i < MIN(PASS_FREQ_MAX, VECSAMPS_MONO/2/5) ; i++) {
			// Harmonic Product Spectrum
			float tmp = fft[fft_i][2*i+1] * fft[fft_i][4*i+1] * fft[fft_i][6*i+1]
			                                    * fft[fft_i][8*i+1] * fft[fft_i][10*i+1];

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
		fp=NULL;

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
		if (fft_max_amp[fft_i] > 20.0) {
			LOG("[HPS] f:%fHz (code:%s) (i:%d -> %f)\n", f, code, max_i, fft_max_amp[fft_i]);
		}

	}
	android_CloseAudioDevice(p);
}

void stop_inst_process() {
	i_on = 0;
}

void inst_mute(){
	i_mute = 1;
}
void inst_unmute(){
	i_mute = 0;
}



// Loads wave data chuck of instruments to inst_wave;
int inst_load(){

	int i, j;

	int hard[6];
	int steel[6];
	int steinway[6];

	char readf[100];

	char *root_folder="/data/app/com.rameon.sing/res/raw/";
	char *extension=".dat";
	char *inst[4]={"none","steel_string_acoustic","hard_rock","steinway_grand_piano"};

//#define INST_NONE			0
//#define INST_AUCU_GUITER	1
//#define INST_ELEC_GUITER	2
//#define INST_PIANO		3


	// ex. Where to load Piano C1 data chuck
	//   =>  inst_wave_chunck[INST_PIANO][1][0]
	//          ~ inst_wave_chunck[INST_PIANO][1][VECSAMPS_MONO*2]

	// TODO : load

}


// Called only when amplitude is higher than low-amp cut off threshold
float *get_inst_frame(int id, float f) {
	if (id==INST_NONE){
		return NULL;
	}

	float *current_frame = (float *) malloc(sizeof(float)*VECSAMPS_MONO);

	// TODO : get frame of size VECSAMPS_MONO by linear interpolation


	// TODO : multiply by hanning window


	// return the frame
	return current_frame;
}

