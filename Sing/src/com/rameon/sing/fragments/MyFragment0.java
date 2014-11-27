package com.rameon.sing.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rameon.sing.R;

/*
 * This is an empty placeholder Fragment.
 */
public class MyFragment0 extends Fragment {
	
	private View view;

	public MyFragment0() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		view = View.inflate(getActivity(), R.layout.frag0, null);
		
		return view;
	}


}
