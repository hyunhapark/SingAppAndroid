/*
 sing.c:
 sing main module
 Copyright (c) 2014, HyunHa Park
 All rights reserved.
 */
//#include "asset_io.h"
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#include <fcntl.h>
#include <stdio.h>

#include "opensl_io.h"
#include "asset_io.h"
#include "Utils.h"
#include "sing.h"
#include "fft.h"




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
static float c_frequency[7];

static int b_on = 0;	// base_process running?
static int i_on = 0;	// inst_process running?
static int i_mute = 0;	// inst play mute?



float *get_inst_frame(int id, float f);
float *hann();

char *stpcpy(char *s1, const char *s2){
	return strcpy(s1,s2)+strlen(s2);
}

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
	float fft[4][2 * VECSAMPS_MONO + 1], fft_max_amp[4];
//	float fft_aged_sum[2 * VECSAMPS_MONO + 1];				// TODO: initialize
	int fft_i = 0;						// circular index of fft
	float *inst_frame[4];
	int inst_frame_i = 0;				// circular index of inst_frame
	float outbuffer[VECSAMPS_STEREO/4];

	int curr_inst = INST_ELEC_GUITER;

//	memset (inst_frame[0], 0, VECSAMPS_MONO*4);


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
//			fft_aged_sum[2*i+1] = fft_aged_sum[2*i+1]/2 + fft[fft_i][2*i+1];
//			fft[fft_i][2*i+1] = fft_aged_sum[2*i+1];
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

		char * code=0;
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
		if (fft_max_amp[fft_i] > 25.0) {
			LOG("[HPS] f:%fHz (code:%s) (i:%d -> %f)\n", f, code, max_i, fft_max_amp[fft_i]);
		}else{
			curr_inst = INST_NONE;
		}
//		inst_frame[inst_frame_i] = get_inst_frame(curr_inst, f);
		inst_frame[inst_frame_i] = get_inst_frame(curr_inst, 440.0);//XXX

		inst_frame[inst_frame_i] = inst_frame[inst_frame_i]==NULL ? (float *) calloc(sizeof(float)*VECSAMPS_MONO,1) : inst_frame[inst_frame_i];

		int pass = 0;
		for(i=0;i<VECSAMPS_MONO/4;i++){
			if(inst_frame[0]==NULL || inst_frame[1]==NULL || inst_frame[2]==NULL || inst_frame[3]==NULL ){
				pass = 1;
				break;
			}
			outbuffer[2*i] =  inst_frame[inst_frame_i][i] + inst_frame[(inst_frame_i+1)%4][3*VECSAMPS_MONO/4+i]
				   + inst_frame[(inst_frame_i+2)%4][2*VECSAMPS_MONO/4+i] + inst_frame[(inst_frame_i+3)%4][VECSAMPS_MONO/4+i];
			outbuffer[2*i] /= 2.;
			outbuffer[2*i+1] = outbuffer[2*i];
		}
		inst_frame_i = (inst_frame_i+1)%4;

		if(pass) continue;

		android_AudioOut(p, outbuffer,  VECSAMPS_STEREO/4);
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
	LOG("%s() start.(L%d)", __FUNCTION__, __LINE__);
	// ex. Where to load Piano C1 data chuck
	//   =>  inst_wave_chunck[INST_PIANO][1][0]
	//          ~ inst_wave_chunck[INST_PIANO][1][VECSAMPS_MONO*2]

	int i, j, k, retval=0;

	FILE *fp=0;

	char *extension=".dat";
	char *inst[4]={"none", "steel_string_acoustic", "hard_rock", "steinway_grand_piano"};
//	char *inst[4];
//	inst[0] = "none";
//	inst[1] = "steel_string_acoustic";
//	inst[2] = "hard_rock";
//	inst[3] = "steinway_grand_piano";

	char *num[6];
	num[0] = "_c1";
	num[1] = "_c2";
	num[2] = "_c3";
	num[3] = "_c4";
	num[4] = "_c5";
	num[5] = "_c6";

	char path[100];

    int n;
    char in[BUFFERFRAMES*2];


	//LOG("%s() loading files. (L%d)", __FUNCTION__, __LINE__);

    for(i=1; i<4; i++)
    {
    	for(j=0; j<6; j++)
    	{
    		memset(path, 0, 100);

			strcat(path, inst[i]);
			strcat(path, num[j]);
			strcat(path, extension);

			//LOG("%s() open file %s. (L%d)", __FUNCTION__, path, __LINE__);
    		fp = android_fopen(path, "rb");
    		//LOG("%s() loading %s - fp : 0x%X (L%d)", __FUNCTION__, path, fp, __LINE__);
    		if (fp==NULL){
    			retval++;
    			LOG("asset %s not found.",path);
    		}
        	n=0;
        	fread(in,1,2*BUFFERFRAMES,fp);
        	for(k=0; k<BUFFERFRAMES; k++)
        		inst_wave_chunck[i][j+1][k] = (short)(in[k*2]*256 + in[k*2+1]);
        	fclose(fp);
    	}

    }

	//LOG("%s() calculating freqs. (L%d)", __FUNCTION__, __LINE__);
    //c_frequency
    c_frequency[5]= 440*pow(2,1/4); //c5

    c_frequency[6]= c_frequency[5]*2;

    c_frequency[4]= c_frequency[5]/2.;
    c_frequency[3]= c_frequency[5]/4.;
    c_frequency[2]= c_frequency[5]/8.;
    c_frequency[1]= c_frequency[5]/16.;
    c_frequency[0]= c_frequency[5]/32.;


	LOG("%s() end. (L%d)", __FUNCTION__, __LINE__);
    return retval;
}

void debug_save_csv(){
	LOG("debug_save_csv() start",0);

	FILE *fp = fopen("/sdcard/piano.csv","w+");
	if(fp==NULL){
		LOG("file open err.",0);
		return;
	}

	float *frame = get_inst_frame(INST_ELEC_GUITER, c_frequency[5]);
	if (frame == NULL){
		LOG("frame returning NULL",0);
		return;
	}

	int i;
	for(i=0; i<VECSAMPS_MONO ; i++){
		fprintf(fp, "%d,%f\n",i,frame[i]);
	}
	fclose(fp);
	LOG("debug_save_csv() end",0);
}

// Called only when amplitude is higher than low-amp cut off threshold
float *get_inst_frame(int id, float f) {


	int i, j;

	float wave[VECSAMPS_MONO*2];
	float near_f;
	int a, oct;

	float *current_frame = (float *) calloc(sizeof(float)*VECSAMPS_MONO,1);
	if(current_frame==NULL){
		LOG("no memory",0);
		return NULL;
	}

	if (id==INST_NONE){
		LOG("INST_NONE",0);
		return current_frame;
	}

	if(f>=c_frequency[6]*2) {
		LOG("f out of range.",0);
		return current_frame;
	} else if(f>=c_frequency[6])
		oct=6;
	else if(f>=c_frequency[5])
		oct=5;
	else if(f>=c_frequency[4])
		oct=4;
	else if(f>=c_frequency[3])
		oct=3;
	else if(f>=c_frequency[2])
		oct=2;
	else if(f>=c_frequency[1])
		oct=1;
	else {
		LOG("f out of range.",0);
		return current_frame;
	}
	memcpy(wave, inst_wave_chunck[id][oct], VECSAMPS_MONO*2*sizeof(float));
	near_f=c_frequency[oct];

	// get frame of size VECSAMPS_MONO by linear interpolation
	for(i=0; i<VECSAMPS_MONO; i++)
	{
		a = floor( i*(f/near_f) );
		current_frame[i]= (wave[a+1]-wave[a]) * (i*(f/near_f)-a) + wave[a];
	}

	// multiply by hanning window
	float *h = hann();

	for(i=0; i<VECSAMPS_MONO; i++){
		current_frame[i] *= h[i];
	}

	// return the frame
	return current_frame;
}

float *hann()
{
	float *window;
	window= calloc(VECSAMPS_MONO, sizeof(float));

	int n;

	for (n = 0; n < VECSAMPS_MONO; n++)
	{
		window[n] = 0.5 * (1. - cos(2. * M_PI * n / (VECSAMPS_MONO)));
	}

	return window;
}


