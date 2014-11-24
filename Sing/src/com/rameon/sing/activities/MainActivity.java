package com.rameon.sing.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.androidquery.AQuery;
import com.rameon.sing.R;
import com.rameon.sing.fragments.MyFragment1;
import com.rameon.sing.fragments.MyFragment2;
import com.rameon.sing.fragments.MyFragment3;
import com.rameon.sing.fragments.MyFragment4;

public class MainActivity extends FragmentActivity implements OnClickListener {

	FragmentManager fm = null;
	FragmentTransaction ft = null;
	MyFragment1 f1 = null;
	MyFragment2 f2 = null;
	MyFragment3 f3 = null;
	MyFragment4 f4 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		f1 = new MyFragment1();
		f2 = new MyFragment2();
		f3 = new MyFragment3();
		f4 = new MyFragment4();

		ft.replace(R.id.mainView, f1, "f1")
				.setTransition(FragmentTransaction.TRANSIT_NONE)
				.commit();

		findViewById(R.id.tab1).setOnClickListener(this);
		findViewById(R.id.tab2).setOnClickListener(this);
		findViewById(R.id.tab3).setOnClickListener(this);
		findViewById(R.id.tab4).setOnClickListener(this);
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
			break;
		case R.id.tab2:
			ft.replace(R.id.mainView, f2, "f2");
			aq.id(R.id.tab2).image(R.drawable.tab_2_on);
			aq.id(R.id.tab1).image(R.drawable.tab_1_off);
			aq.id(R.id.tab3).image(R.drawable.tab_3_off);
			aq.id(R.id.tab4).image(R.drawable.tab_4_off);
			break;
		case R.id.tab3:
			ft.replace(R.id.mainView, f3, "f3");
			aq.id(R.id.tab3).image(R.drawable.tab_3_on);
			aq.id(R.id.tab1).image(R.drawable.tab_1_off);
			aq.id(R.id.tab2).image(R.drawable.tab_2_off);
			aq.id(R.id.tab4).image(R.drawable.tab_4_off);
			break;
		case R.id.tab4:
			ft.replace(R.id.mainView, f4, "f4");
			aq.id(R.id.tab4).image(R.drawable.tab_4_on);
			aq.id(R.id.tab1).image(R.drawable.tab_1_off);
			aq.id(R.id.tab2).image(R.drawable.tab_2_off);
			aq.id(R.id.tab3).image(R.drawable.tab_3_off);
			break;
		}
		ft.setTransition(FragmentTransaction.TRANSIT_NONE);
		ft.commit();
	}
}
