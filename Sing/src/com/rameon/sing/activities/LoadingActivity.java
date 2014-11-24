package com.rameon.sing.activities;

import com.rameon.sing.R;
import com.rameon.sing.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		
		int delayLength=1000;
		
		
		if(!new Handler().postDelayed(new Runnable(){

			public void run() {

				startActivity(new Intent(getApplicationContext(), MainActivity.class));

				finish();

			}

		}, delayLength)){
			finish();
		}
	}
}
