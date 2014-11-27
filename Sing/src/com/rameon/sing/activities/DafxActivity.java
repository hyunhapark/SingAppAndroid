/*
 * Voicesmith <http://voicesmith.jurihock.de/>
 *
 * Copyright (c) 2011-2014 Juergen Hock
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
import android.view.View;
import android.view.View.OnClickListener;

import com.rameon.sing.DAFX;
import com.rameon.sing.R;
import com.rameon.sing.Utils;
import com.rameon.sing.services.DafxService;
import com.rameon.sing.services.ServiceFailureReason;
import com.rameon.sing.services.ServiceListener;

public final class DafxActivity extends AudioServiceActivity<DafxService>
	implements
	OnClickListener, ServiceListener
{

	public DafxActivity()
	{
		super(DafxService.class);
	}

	/**
	 * Initializes the activity, its layout and widgets.
	 * */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_dafx);
		findViewById(R.id.button1).setOnClickListener(this);
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

	public void onClick(View view)
	{
		if (getService().isThreadRunning())
		{
			getService().stopThread(false);
		}
		else
		{
			getService().startThread();
		}

	}
	


	
	public void onServiceFailed(ServiceFailureReason reason)
	{

		new Utils(this).toast(getString(R.string.ServiceFailureMessage));
	}
}
