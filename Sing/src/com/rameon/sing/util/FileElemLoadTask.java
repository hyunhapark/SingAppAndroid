package com.rameon.sing.util;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rameon.sing.R;
import com.rameon.sing.activities.FileManagerActivity;
import com.rameon.sing.data.FileElem;

public class FileElemLoadTask extends AsyncTask<String, Integer, ArrayList<FileElem>>{

	
	Context ctx;
	
	public FileElemLoadTask() {}
	public FileElemLoadTask(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	protected ArrayList<FileElem> doInBackground(String... params) {
		File file = new File(params[0]);
		String[] fList;
		ArrayList<FileElem> data = new ArrayList<FileElem>();  
		if(file.exists() && file.isDirectory())
		{
			fList = file.list();
			
			for(int i=0; i<fList.length;i++)
			{
				File sonfile = new File(params[0]+"/"+fList[i]);
				Time t = new Time();
				t.set(sonfile.lastModified());
				data.add(new FileElem(fList[i], t.format("%Y/%m/%d %H:%M:%S"),
						(int) (Math.random() * 24) + ":"
								+ (int) (Math.random() * 60) + "", params[0]+"/"+fList[i]));
			}
		}
		
		return data;
	}

	@Override
	protected void onPostExecute(ArrayList<FileElem> data) {
		int i;
		for (i=0 ; i<data.size() ; i++){
			FileManagerActivity.data.add(data.get(i));
		}
		FileManagerActivity.aa.notifyDataSetChanged();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}


	
	

}
