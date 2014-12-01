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

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
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
				((MainActivity)ctx).getService().startThread();
			} else {
				
				if (((MainActivity)ctx).getService().isThreadRunning())
				{
					((MainActivity)ctx).getService().stopThread(false);
				}
			}
			aq.id(R.id.imageMic).image(
					mic_on ? R.drawable.wrap_mic_on : R.drawable.wrap_mic_off);
			break;

		case R.id.imageRec:
			rec_on = rec_on ? false : true;
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
