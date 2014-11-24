package com.rameon.sing.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.rameon.sing.R;
import com.rameon.sing.activities.FileManagerActivity;
import com.rameon.sing.opensl.SingModule;

public class MyFragment1 extends Fragment implements OnClickListener {

	View view;
	
	AQuery aq;
	boolean mic_on, rec_on;

	private Thread thread;
	
	public MyFragment1() {
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

		
		return view;
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		
		SingModule.stop_base_process();
    	try {
    		thread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (NullPointerException npe) {
			// pass
		}
    	thread = null;
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
						SingModule.start_base_process();
					}
				};
				thread.start();
			}else{
				SingModule.stop_base_process();
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
			startActivity(new Intent(getActivity(), FileManagerActivity.class));
			break;

		default:
			break;
		}
	}

}
