/*
 * Sing
 *
 * Copyright (c) 2014 HyunHa Park
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.rameon.sing.fragments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.androidquery.AQuery;
import com.rameon.sing.R;
import com.rameon.sing.Utils;
import com.rameon.sing.activities.FileManagerActivity;
import com.rameon.sing.activities.MainActivity;

public class MyFragment1 extends Fragment implements OnClickListener {

	View view;

	AQuery aq;
	boolean mic_on, rec_on;

	private SeekBar seekBarVolume;
	private AudioManager audioManager;
	Context ctx;

	public MyFragment1() {
	}public MyFragment1(Context ctx){
		this.ctx = ctx;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		view = View.inflate(getActivity(), R.layout.frag1, null);

		mic_on = false;
		rec_on = false;

		aq = new AQuery(view);

		view.findViewById(R.id.imageMic).setOnClickListener(this);
		view.findViewById(R.id.imageRec).setOnClickListener(this);
		view.findViewById(R.id.imageFile).setOnClickListener(this);

		seekBarVolume = (SeekBar) view.findViewById(R.id.volumeBar);
		audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		final int nMax = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		seekBarVolume.setMax(nMax);

		seekBarVolume
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					boolean valid = false;
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						valid = false;
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						valid = true;
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if(valid){
							audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
									progress, 0);
						}
					}
				});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		int nCurrentVolumn = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		seekBarVolume.setProgress(nCurrentVolumn);
	}
	
	

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int nCurrentVolumn = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			nCurrentVolumn++;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			nCurrentVolumn--;
		}
		seekBarVolume.setProgress(nCurrentVolumn);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				nCurrentVolumn, 0);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();

		
		if(ctx==null) return;
		if(((MainActivity)ctx).getService()==null) return;
		if (((MainActivity)ctx).getService().isThreadRunning())
		{
			((MainActivity)ctx).getService().stopThread(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageMic:
			mic_on = mic_on ? false : true;
			if (mic_on) {
				if(((MainActivity)ctx).getService()!=null){
					((MainActivity)ctx).getService().startThread();
				}
			} else {
				
				if (((MainActivity)ctx).getService()!=null && ((MainActivity)ctx).getService().isThreadRunning())
				{
					((MainActivity)ctx).getService().stopThread(false);
				}
			}
			aq.id(R.id.imageMic).image(
					mic_on ? R.drawable.wrap_mic_on : R.drawable.wrap_mic_off);
			break;

		case R.id.imageRec:
			rec_on = rec_on ? false : true;
			
			if(rec_on){
				new Utils(ctx).toast("file open error.");
//				mic_on = false;
//				aq.id(R.id.imageMic).image(R.drawable.wrap_mic_off);
//				
//				
//				SharedPreferences sp = ctx.getSharedPreferences("recFileName", ctx.MODE_PRIVATE);
//				String fn = sp.getString("fileName", "Untitled");
//				int fs = sp.getInt("fileSeq", 1);
//				while(new File(new String(
//						Environment.getExternalStorageDirectory().getPath()
//						+ "/com.rameon.sing/waves/"+String.format("%s-%03d.wav", fn, fs))).exists()){
//					fs++;
//				}
//				MainActivity.currRecFile = new File(String.format("%s-%03d.wav", fn, fs));
//				FileWriter fw = null;
//				try {
//					fw = new FileWriter(MainActivity.currRecFile);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				MainActivity.currRecBW = new BufferedWriter(fw);
//				char[] riff = {0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00,
//						0x57, 0x41, 0x56, 0x45, 0x66, 0x6D, 0x74, 0x20,
//						0x10, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00,
//						0x44, 0xAC, 0x00, 0x00, 0x88, 0x58, 0x01, 0x00,
//						0x02, 0x00, 0x10, 0x00, 0x64, 0x61, 0x74, 0x61,
//						0x00, 0x00, 0x00, 0x00};
//				try {
//					MainActivity.currRecBW.write(riff, 0, 44);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				MainActivity.rec_on = true;
//				//rec_start(String.format("%s-%03d.wav", fn, fs));
//				SharedPreferences.Editor e = sp.edit();
//				e.putString("fileName", fn);
//				e.putInt("fileSeq", fs+1);
//				e.commit();
//				
			}else{
				new Utils(ctx).toast("녹음파일 저장에 실패하였습니다.");
//				mic_on = true;
//				aq.id(R.id.imageMic).image(R.drawable.wrap_mic_on);
//				
//				MainActivity.rec_on= false;
//				char[] riff = {0x52, 0x49, 0x46, 0x46, 0x00, 0x00, 0x00, 0x00,
//						0x57, 0x41, 0x56, 0x45, 0x66, 0x6D, 0x74, 0x20,
//						0x10, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00,
//						0x44, 0xAC, 0x00, 0x00, 0x88, 0x58, 0x01, 0x00,
//						0x02, 0x00, 0x10, 0x00, 0x64, 0x61, 0x74, 0x61,
//						0x00, 0x00, 0x00, 0x00};
//
//				FileWriter fw = null;
//				try {
//					fw = new FileWriter(MainActivity.currRecFile,false);
//					fw.write(riff, 0, 4);
//					int print = MainActivity.rec_size+36;
//					fw.write(String.format("%c%c%c%c",print&255, (print>>8)&255, (print>>16)&255, (print>>24)&255));
//					fw.write(riff, 8, 32);
//					print -= 36;
//					fw.write(String.format("%c%c%c%c",print&255, (print>>8)&255, (print>>16)&255, (print>>24)&255));
//					fw.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				MainActivity.rec_size = 0;
//				MainActivity.currRecFile = null;
			}
			aq.id(R.id.imageRec).image(
					rec_on ? R.drawable.wrap_rec_on : R.drawable.wrap_rec_off);
			break;

		case R.id.imageFile:
			startActivity(new Intent(getActivity(), FileManagerActivity.class));
			break;

		default:
			break;
		}
	}

}
