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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.androidquery.AQuery;
import com.rameon.sing.DAFX;
import com.rameon.sing.R;
import com.rameon.sing.Utils;
import com.rameon.sing.fragments.MyFragment1;
import com.rameon.sing.fragments.MyFragment2;
import com.rameon.sing.fragments.MyFragment3;
import com.rameon.sing.fragments.MyFragment4;
import com.rameon.sing.services.DafxService;
import com.rameon.sing.services.ServiceFailureReason;
import com.rameon.sing.services.ServiceListener;

public class MainActivity extends AudioServiceFragmentActivity<DafxService>
implements
OnClickListener, ServiceListener {

	FragmentManager fm = null;
	FragmentTransaction ft = null;
	MyFragment1 f1 = null;
	MyFragment2 f2 = null;
	MyFragment3 f3 = null;
	MyFragment4 f4 = null;
	
	
	int curr_tab;
	
	
	public MainActivity()
	{
		super(DafxService.class);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		f1 = new MyFragment1(this);
		f2 = new MyFragment2(this);
		f3 = new MyFragment3();
		f4 = new MyFragment4();

		ft.replace(R.id.mainView, f1, "f1")
				.setTransition(FragmentTransaction.TRANSIT_NONE)
				.commit();
		curr_tab = 1;

		findViewById(R.id.tab1).setOnClickListener(this);
		findViewById(R.id.tab2).setOnClickListener(this);
		findViewById(R.id.tab3).setOnClickListener(this);
		findViewById(R.id.tab4).setOnClickListener(this);
	}

	@Override
	protected void onServiceConnected()
	{
		
		new Utils(this).log("DafxActivity founds the audio service.");

//		getService().setActivityVisible(true, this.getClass());
		getService().setListener(this);
		getService().setDafx(DAFX.Transpose);
		getService().setThreadPreferences(Integer.toString(4));
	}

	@Override
	protected void onServiceDisconnected()
	{
		new Utils(this).log("DafxActivity losts the audio service.");

		if (!this.isFinishing())
		{
//			getService().setActivityVisible(false, this.getClass());
		}
		try{
			getService().setListener(null);
		}catch(Exception e){}
	}


	
	public void onServiceFailed(ServiceFailureReason reason)
	{

		new Utils(this).toast(getString(R.string.ServiceFailureMessage));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			switch(curr_tab){
			case 1: f1.onKeyDown(keyCode, event); break;
			case 2: f2.onKeyDown(keyCode, event); break;
			case 3: case 4: return false;
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return false;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		AQuery aq = new AQuery(this);
		ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_NONE);
		switch (v.getId()) {
		case R.id.tab1:
			ft.replace(R.id.mainView, f1, "f1");
			aq.id(R.id.tab1).image(R.drawable.tab_1_on);
			aq.id(R.id.tab2).image(R.drawable.tab_2_off);
			aq.id(R.id.tab3).image(R.drawable.tab_3_off);
			aq.id(R.id.tab4).image(R.drawable.tab_4_off);
			curr_tab = 1;
			break;
		case R.id.tab2:
			ft.replace(R.id.mainView, f2, "f2");
			aq.id(R.id.tab2).image(R.drawable.tab_2_on);
			aq.id(R.id.tab1).image(R.drawable.tab_1_off);
			aq.id(R.id.tab3).image(R.drawable.tab_3_off);
			aq.id(R.id.tab4).image(R.drawable.tab_4_off);
			curr_tab = 2;
			break;
		case R.id.tab3:
			ft.replace(R.id.mainView, f3, "f3");
			aq.id(R.id.tab3).image(R.drawable.tab_3_on);
			aq.id(R.id.tab1).image(R.drawable.tab_1_off);
			aq.id(R.id.tab2).image(R.drawable.tab_2_off);
			aq.id(R.id.tab4).image(R.drawable.tab_4_off);
			curr_tab = 3;
			break;
		case R.id.tab4:
			ft.replace(R.id.mainView, f4, "f4");
			aq.id(R.id.tab4).image(R.drawable.tab_4_on);
			aq.id(R.id.tab1).image(R.drawable.tab_1_off);
			aq.id(R.id.tab2).image(R.drawable.tab_2_off);
			aq.id(R.id.tab3).image(R.drawable.tab_3_off);
			curr_tab = 4;
			break;
		}
		ft.setTransition(FragmentTransaction.TRANSIT_NONE);
		ft.commit();
	}
}
