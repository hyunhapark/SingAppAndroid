package com.rameon.sing.fragments;

import com.androidquery.AQuery;
import com.rameon.sing.R;
import com.rameon.sing.R.layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyFragment4 extends Fragment {

	
	
	public MyFragment4() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.frag4, null);
		AQuery aq = new AQuery(view);
		return view;
	}



}
