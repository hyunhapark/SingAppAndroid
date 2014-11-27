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
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.rameon.sing.R;
import com.rameon.sing.data.FileElem;
import com.rameon.sing.util.FileElemLoadTask;

public class FileManagerActivity extends ListActivity {

	public static ArrayList<FileElem> data;
	private AQuery aq;
	private Thread thread;
	public static ArrayAdapter<FileElem> aa;
	//new String("file:///sdcard/com.rameon.sing/waves/")
	//Uri uri;
	MediaPlayer mp;
	private Thread t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_manager);

		aq = new AQuery(this);

		data = new ArrayList<FileElem>();

		aa = new ArrayAdapter<FileElem>(this,
				R.layout.file_list_item, data) {

			class ViewHolder {
				TextView tv1;
				TextView tv2;
				TextView tv3;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder vh = null;
				FileElem item = getItem(position);

				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(), R.layout.file_list_item, null);
					vh = new ViewHolder();
					vh.tv1 = (TextView) convertView.findViewById(R.id.textView1);
					vh.tv2 = (TextView) convertView.findViewById(R.id.textView2);
					vh.tv3 = (TextView) convertView.findViewById(R.id.textView3);

					convertView.setTag(vh);
				} else {
					vh= (ViewHolder) convertView.getTag();
				}
				vh.tv1.setText(item.getFileName());
				vh.tv2.setText(item.getDate());
				vh.tv3.setText(item.getLength());
				
				return convertView;
			}

		};

		setListAdapter(aa);
	
		new FileElemLoadTask(this).execute(new String("/sdcard/com.rameon.sing/waves"));
		
		this.getListView().setLongClickable(true);
		this.getListView().setOnItemLongClickListener(
			new OnItemLongClickListener() {

				private FileManagerDialog mFileManagerDialog;

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View v, int position, long id) {
					FileElem fe = data.get(position);
					mFileManagerDialog = new FileManagerDialog();
					mFileManagerDialog.show(getFragmentManager(), "MYTAG");
					return true;
				}
		});
	}
	

	public static class FileManagerDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(
					getActivity());
			LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
			mBuilder.setView(mLayoutInflater.inflate(
					R.layout.dialog_file_manager, null));
			mBuilder.setTitle("Dialog Title");
			// mBuilder.setMessage("Dialog Message");
			return mBuilder.create();
		}

		@Override
		public void onStop() {
			super.onStop();
		}

	}

	
	
	// /sdcard/com.rameon.sing/waves/
	private void getData() {
		Time t = new Time();
		t.setToNow();
		int i;
		for (i = 1; i <= 16; i++) {
			data.add(new FileElem("title" + i, t.format("%Y/%m/%d"),
					(int) (Math.random() * 24) + ":"
							+ (int) (Math.random() * 60) + "", "/some/path"));
		}
		
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(t!=null){
			t.interrupt();
		}
		if(mp!=null)
			mp.release();
		mp = MediaPlayer.create(this, Uri.fromFile(new File(new String("///sdcard/com.rameon.sing/waves/"+data.get(position).getFileName()))));
		mp.start();
		t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean end = false;
				
				while (!end){
					//TODO
					
					
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "mp-thread");
		t.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(t!=null){
			t.interrupt();
		}
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
}




