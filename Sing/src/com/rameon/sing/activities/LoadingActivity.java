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

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;

import com.rameon.sing.R;
import com.rameon.sing.Utils;
import com.rameon.sing.opensl.SingModule;
import com.rameon.sing.opensl2.AssetLoader;

public class LoadingActivity extends Activity {

	boolean canceled;
	private AssetManager mgr;
	
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
		
		// Load asset manager
		mgr = getResources().getAssets();
		AssetLoader.set_asset_manager(mgr);
		
		// mkdir
		File f = new File(new String(
				Environment.getExternalStorageDirectory().getPath()
						+ "/com.rameon.sing/waves"));
		if(!f.exists()){
			f.mkdirs();
		}
		
		SingModule.inst_load();
		
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
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK){
			canceled = true;
			finish();
		}
		return false;
		
	}
}
