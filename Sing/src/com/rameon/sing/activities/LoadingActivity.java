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

package com.rameon.sing.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.rameon.sing.R;
import com.rameon.sing.Utils;

public class LoadingActivity extends Activity {

	boolean canceled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		
		int delayLength=1000;
		canceled = false;
		
		
		// Load Native Shared Libraries (.so files)
		try {
			Utils.loadNativeLibrary();
		} catch (UnsatisfiedLinkError e) {
			canceled = true;
			finish();
		}
		
		if(!new Handler().postDelayed(new Runnable(){

			public void run() {
				if(!canceled){
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
				}

				finish();

			}

		}, delayLength)){
			finish();
		}
	}
}
