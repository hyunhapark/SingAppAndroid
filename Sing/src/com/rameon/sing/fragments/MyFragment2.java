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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.androidquery.AQuery;
import com.rameon.sing.R;
import com.rameon.sing.activities.DafxActivity;
import com.rameon.sing.opensl.SingModule;

public class MyFragment2 extends Fragment implements OnClickListener {

	View view;
	
	AQuery aq;
	boolean mic_on, rec_on;

	private SeekBar seekBarVolume;
	private AudioManager audioManager;

	
	Context ctx;
	
	private Thread thread;
	
	public MyFragment2() {
	}public MyFragment2(Context ctx){
		this.ctx = ctx;
	}

	@SuppressLint({ "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = View.inflate(getActivity(), R.layout.frag2, null);
		
		if ( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ){
		    AudioManager am = ( AudioManager ) ctx.getSystemService( Context.AUDIO_SERVICE );
		    int sampleRate = Integer.parseInt( am.getProperty( AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE ));
		    int bufferSize = Integer.parseInt( am.getProperty( AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER ));
		    Log.v("Sing", "sr:"+sampleRate +", buf:"+bufferSize);
		}
		
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
		
		

//		thread = new Thread() {
//			public void run() {
//				Log.v("Sing", "start csv.");
//				SingModule.debug_save_csv();
//				Log.v("Sing", "complete csv.");
//			}
//		};
//		thread.start();
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		int nCurrentVolumn = audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		seekBarVolume.setProgress(nCurrentVolumn);
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		
		SingModule.stop_inst_process();
    	try {
    		thread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (NullPointerException npe) {
			// pass
		}
    	thread = null;
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageMic:
			mic_on = mic_on ? false : true;
			if(mic_on){
				thread = new Thread() {
					public void run() {
						setPriority(Thread.MAX_PRIORITY);
						SingModule.start_inst_process();
					}
				};
				thread.start();
			}else{
				SingModule.stop_inst_process();
		    	try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (NullPointerException npe) {
					// pass
				}
		    	thread = null;
			}
			aq.id(R.id.imageMic).image(mic_on ? 
					R.drawable.wrap_mic_on : R.drawable.wrap_mic_off);
			break;

		case R.id.imageRec:
			rec_on = rec_on ? false : true;
			aq.id(R.id.imageRec).image(rec_on ? 
					R.drawable.wrap_rec_on : R.drawable.wrap_rec_off);
			break;

		case R.id.imageFile:
			startActivity(new Intent(getActivity(), DafxActivity.class));
			break;

		default:
			break;
		}
	}



}
