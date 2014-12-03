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
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.rameon.sing.R;
import com.rameon.sing.data.FileElem;
import com.rameon.sing.util.FileElemLoadTask;

public class FileManagerActivity extends ListActivity implements
      OnClickListener {

   public static ArrayList<FileElem> data;
   private AQuery aq;
   private Thread thread;
   public static ArrayAdapter<FileElem> aa;
   MediaPlayer mp;
   private Thread t;
   private int seekBarMax;
   private int position;
   private SeekBar seekBar;
   public int dialogDeletePosition;
   private VolumeDialog mVolumeDialog;
   private SeekBar dialogVolumeBar;
   private AudioManager audioManager;
   View view;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_file_manager);

      aq = new AQuery(this);

      seekBarMax = ((SeekBar) findViewById(R.id.seekBar)).getMax();

      data = new ArrayList<FileElem>();

      aa = new ArrayAdapter<FileElem>(this, R.layout.file_list_item, data) {

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
               convertView = View.inflate(getApplicationContext(),
                     R.layout.file_list_item, null);
               vh = new ViewHolder();
               vh.tv1 = (TextView) convertView
                     .findViewById(R.id.textView1);
               vh.tv2 = (TextView) convertView
                     .findViewById(R.id.textView2);
               vh.tv3 = (TextView) convertView
                     .findViewById(R.id.textView3);

               convertView.setTag(vh);
            } else {
               vh = (ViewHolder) convertView.getTag();
            }
            vh.tv1.setText(item.getFileName());
            vh.tv2.setText(item.getDate());
            vh.tv3.setText(item.getLength());

            return convertView;
         }

      };

      setListAdapter(aa);

      new FileElemLoadTask(this).execute(new String(Environment
            .getExternalStorageDirectory().getPath()
            + "/com.rameon.sing/waves"));

      aq.find(R.id.buttonPlay).clicked(this);
      aq.find(R.id.buttonFforward).clicked(this);
      aq.find(R.id.buttonRewind).clicked(this);
      aq.find(R.id.buttonShare).clicked(this);
      aq.find(R.id.buttonStop).clicked(this);
      aq.find(R.id.buttonTrash).clicked(this);
      aq.find(R.id.buttonVolume).clicked(this);
      
      seekBar = (SeekBar) findViewById(R.id.seekBar);
      seekBar
      .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

         boolean valid = false;
         
         @Override
         public void onStopTrackingTouch(SeekBar seekBar) {
            valid = false;
         }

         @Override
         public void onStartTrackingTouch(SeekBar seekBar) {
            valid = true;
         }

         @Override
         public void onProgressChanged(SeekBar seekBar,
               int progress, boolean fromUser) {
            if(valid){
               if(mp == null)
                  return;
               else if(mp.isPlaying())
               {
                  mp.seekTo(mp.getDuration()*progress/seekBar.getMax());
               }
               else{
                  mp.reset();
                  mp = MediaPlayer.create(null, Uri.fromFile(new File(new String(
                        Environment.getExternalStorageDirectory().getPath()
                              + "/com.rameon.sing/waves/"
                              + data.get(position).getFileName()))));
                  mp.seekTo(mp.getDuration()*progress/seekBar.getMax());
                  int cur = mp.getCurrentPosition();
                  String s_cur = new String((cur / 3600000 > 0 ? cur
                        / 3600000 + ":" : "")
                        + (cur / 60000 > 9 ? cur / 60000 + ""
                              : "0" + cur / 60000 + "")
                        + ":"
                        + (cur / 1000 > 9 ? cur / 1000 + ""
                              : "0" + cur / 1000));
                  aq.find(R.id.timeCurrent).text(s_cur);
               }
            }
         }
      });
   }

   public static class FileManagerDialog extends DialogFragment {

      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState) {
         final CharSequence[] items = {"Share", "Delete", "Rename", "Info"};
         AlertDialog.Builder mBuilder = new AlertDialog.Builder(
               getActivity());
         LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
         //mBuilder.setView(mLayoutInflater.inflate(
               //R.layout.dialog_file_manager, null));
         mBuilder.setItems(items, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // TODO Auto-generated method stub
               switch(which){
               case 1:
                  /*File toDelete = new File(
                        new String(
                              Environment
                                    .getExternalStorageDirectory()
                                    .getPath()
                                    + "/com.rameon.sing/waves/"
                                    + data.get(
                                          dialogDeletePosition)
                                          .getFileName()));
                  toDelete.delete();
                  aa.remove(aa.getItem(dialogDeletePosition));
                  aa.notifyDataSetChanged();*/
                  Toast.makeText(getActivity(), "dddddd", Toast.LENGTH_SHORT).show();
                  break;
               }
            }
         });
         // mBuilder.setMessage("Dialog Message");

         return mBuilder.create();
      }

      @Override
      public void onStop() {
         super.onStop();
      }

   }
   
   public static class VolumeDialog extends DialogFragment {

      @Override
      public Dialog onCreateDialog(Bundle savedInstanceState) {
         AlertDialog.Builder mBuilder = new AlertDialog.Builder(
               getActivity());
         LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();
         mBuilder.setView(mLayoutInflater.inflate(
               R.layout.dialog_file_manager, null));

         return mBuilder.create();
         /*dialogVolumeBar = (SeekBar) view.findViewById(R.id.dialogVolumeSeekBar);
         audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
         final int nMax = audioManager
               .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
         dialogVolumeBar.setMax(nMax);

         dialogVolumeBar
               .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                  boolean valid = false;
                  
                  @Override
                  public void onStopTrackingTouch(SeekBar seekBar) {
                     valid = false;
                  }

                  @Override
                  public void onStartTrackingTouch(SeekBar seekBar) {
                     valid = true;
                  }

                  @Override
                  public void onProgressChanged(SeekBar seekBar,
                        int progress, boolean fromUser) {
                     if(valid){
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                              progress, 0);
                     }
                  }
               });*/
      }

      @Override
      public void onStop() {
         super.onStop();
      }

   }


   @Override
   protected void onListItemClick(ListView l, View v, int position, long id) {
      this.position = position;
      aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_on);
      PlayMusic(position);
   }

   @Override
   protected void onPause() {
      super.onPause();
      if (t != null) {
         t.interrupt();
         t=null;
      }
      if (mp != null && mp.isPlaying()) {
         mp.pause();
         aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_off);
      }
   }
   
   protected void runUpdatePlayer() {
      t = new Thread(new Runnable() {

         private String s_cur;
         private int p;

         @Override
         public void run() {

            boolean end = false;

            mp.setOnCompletionListener(new OnCompletionListener() {

               @Override
               public void onCompletion(MediaPlayer mp) {
                  if (t != null) {
                     ((SeekBar) findViewById(R.id.seekBar))
                           .setProgress(seekBarMax);
                     aq.find(R.id.buttonPlay).image(
                           R.drawable.wrap_play_off);
                     aq.find(R.id.timeCurrent).text(
                           aq.find(R.id.timeTotal)
                                 .getText());
                     mp.stop();
                     t.interrupt();
                     t = null;
                  }
               }
            });

            while (!end) {
               int tot = mp.getDuration() - 500 > 0 ? mp.getDuration() - 500
                     : mp.getDuration();
               tot = tot == 0 ? 1 : tot;
               int cur = mp.getCurrentPosition();
               s_cur = new String((cur / 3600000 > 0 ? cur
                     / 3600000 + ":" : "")
                     + (cur / 60000 > 9 ? cur / 60000 + ""
                           : "0" + cur / 60000 + "")
                     + ":"
                     + (cur / 1000 > 9 ? cur / 1000 + ""
                           : "0" + cur / 1000));

               p = cur < tot ? seekBarMax * cur / tot
                     : seekBarMax;
               runOnUiThread(new Runnable() {

                  @Override
                  public void run() {
                     ((SeekBar) findViewById(R.id.seekBar)).setProgress(p);
                     aq.find(R.id.timeCurrent).text(s_cur);
                  }
               });

               try {
                  Thread.sleep(100);
               } catch (InterruptedException e) {
                  end = true;
               }
            }
            if(((SeekBar) findViewById(R.id.seekBar)).getProgress() == ((SeekBar) findViewById(R.id.seekBar)).getMax())
               aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_off);
         }
      }, "mp-timer-thread");
      t.start();
      
   }

   protected void StopPlaying() {
      if (t != null)
      {
         t.interrupt();
         t = null;
      }
      if (((SeekBar) findViewById(R.id.seekBar)).getProgress() == seekBarMax) {
         aq.find(R.id.timeCurrent).text("00:00");
         ((SeekBar) findViewById(R.id.seekBar)).setProgress(0);
         aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_off);
      } else {
         if (mp != null) {
            mp.stop();
            try {
               mp.reset();
               mp.setDataSource(this, Uri.fromFile(new File(new String(
                     Environment.getExternalStorageDirectory().getPath()
                           + "/com.rameon.sing/waves/"
                           + data.get(position).getFileName()))));
            } catch (IllegalArgumentException e) {
               e.printStackTrace();
            } catch (SecurityException e) {
               e.printStackTrace();
            } catch (IllegalStateException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            }
            try {
               mp.prepare();
            } catch (IllegalStateException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
         aq.find(R.id.timeCurrent).text("00:00");
         ((SeekBar) findViewById(R.id.seekBar)).setProgress(0);
         aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_off);
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

   @Override
   public void onClick(View v) {
      switch (v.getId()) {
      case R.id.buttonRewind:
         if (((SeekBar) findViewById(R.id.seekBar)).getProgress() == seekBarMax)
            PlayMusic(position);
         else if (mp == null)
            break;
         if (mp.getDuration() < 25000)
            if (100 * mp.getCurrentPosition() / mp.getDuration() > 40)
               PlayMusic(position);
            else {
               position = (data.size() + position - 1) % data.size();
               PlayMusic(position);
            }
         else if (mp.getCurrentPosition() > 10000)
            PlayMusic(position);
         else {
            position = (data.size() + position - 1) % data.size();
            PlayMusic(position);
         }
         aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_on);
         break;

      case R.id.buttonFforward:
         position = (position + 1) % data.size();
         PlayMusic(position);
         aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_on);
         break;

      case R.id.buttonPlay:
         if (((SeekBar) findViewById(R.id.seekBar)).getProgress() == seekBarMax) {
            PlayMusic(position);
            aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_on);
         } else if (((SeekBar) findViewById(R.id.seekBar)).getProgress() == 0) {
            PlayMusic(position);
            aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_on);
         } else if (mp.isPlaying()) {
            mp.pause();
            aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_off);
         } else {
            mp.start();
            if (t==null || t.isInterrupted())
               runUpdatePlayer();
            aq.find(R.id.buttonPlay).image(R.drawable.wrap_play_on);
         }
         break;

      case R.id.buttonShare:

         break;

      case R.id.buttonStop:
         StopPlaying();
         break;

      case R.id.buttonTrash:
         if (mp != null) {
            new AlertDialog.Builder(this)
                  .setTitle("Delete")
                  .setMessage("Do you want to Delete?")
                  .setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {

                           @Override
                           public void onClick(DialogInterface dialog,
                                 int which) {
                              StopPlaying();
                              File toDelete = new File(
                                    new String(
                                          Environment
                                                .getExternalStorageDirectory()
                                                .getPath()
                                                + "/com.rameon.sing/waves/"
                                                + data.get(
                                                      position)
                                                      .getFileName()));
                              toDelete.delete();
                              aa.remove(aa.getItem(position));
                              aa.notifyDataSetChanged();
                              aq.find(R.id.timeTotal).text("00:00");
                              aq.find(R.id.textFileName).text(
                                    "FILENAME.");
                           }
                        })
                  .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog,
                                 int which) {
                           }
                        }).show();
         }
         break;

      case R.id.buttonVolume:
         mVolumeDialog = new VolumeDialog();
         mVolumeDialog.show(getFragmentManager(), "MYTAG");
         //dialogVolumeSeekBar
         break;

      default:
         break;
      }
   }

   private void PlayMusic(int position) {
      if (t != null) {
         t.interrupt();
      }
      if (mp == null)
         mp = MediaPlayer.create(this, Uri.fromFile(new File(new String(
               Environment.getExternalStorageDirectory().getPath()
                     + "/com.rameon.sing/waves/"
                     + data.get(position).getFileName()))));
      else {
         try {
            mp.reset();
            mp.setDataSource(this, Uri.fromFile(new File(new String(
                  Environment.getExternalStorageDirectory().getPath()
                        + "/com.rameon.sing/waves/"
                        + data.get(position).getFileName()))));
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         } catch (SecurityException e) {
            e.printStackTrace();
         } catch (IllegalStateException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }

         try {
            mp.prepare();
         } catch (IllegalStateException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      mp.start();
      int dur = mp.getDuration();
      String s_dur = new String(
            (dur / 3600000 > 0 ? dur / 3600000 + ":" : "")
                  + (dur / 60000 > 9 ? dur / 60000 + "" : "0" + dur
                        / 60000 + "") + ":"
                  + (dur / 1000 > 9 ? dur / 1000 + "" : "0" + dur / 1000));
      aq.find(R.id.textFileName).text(data.get(position).getFileName());
      aq.find(R.id.timeTotal).text(s_dur);
      runUpdatePlayer();
   }
}