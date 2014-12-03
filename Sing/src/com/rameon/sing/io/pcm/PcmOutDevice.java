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

package com.rameon.sing.io.pcm;

import java.io.IOException;
import java.util.Arrays;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.rameon.sing.Preferences;
import com.rameon.sing.Utils;
import com.rameon.sing.activities.MainActivity;
import com.rameon.sing.audio.HeadsetMode;


public final class PcmOutDevice extends PcmDevice
{
    private static final int	WIRED_HEADPHONES_SOURCE		= AudioManager.STREAM_MUSIC;
	private static final int	WIRED_HEADSET_SOURCE		= AudioManager.STREAM_MUSIC;
	private static final int	BLUETOOTH_HEADSET_SOURCE	= AudioManager.STREAM_VOICE_CALL;

	private AudioTrack			output						= null;

	public PcmOutDevice(Context context, HeadsetMode headsetMode)
		throws IOException
	{
		super(context);

		switch (headsetMode)
		{
        case WIRED_HEADPHONES:
            setAudioSource(WIRED_HEADPHONES_SOURCE);
            break;
		case WIRED_HEADSET:
			setAudioSource(WIRED_HEADSET_SOURCE);
			break;
		case BLUETOOTH_HEADSET:
			setSampleRate(8000);
			new Utils(context).log("Sample rate changed to 8000 Hz.");
			setAudioSource(BLUETOOTH_HEADSET_SOURCE);
			break;
		default:
			throw new IOException("Unknown HeadsetMode!");
		}

		init(context);
	}

	public PcmOutDevice(Context context, int sampleRate) throws IOException
	{
		super(context, sampleRate);
		init(context);
	}

	public PcmOutDevice(Context context) throws IOException
	{
		super(context);
		init(context);
	}

	private void init(Context context) throws IOException
	{
		this.setChannels(AudioFormat.CHANNEL_OUT_MONO); // DON'T CHANGE!
		this.setEncoding(AudioFormat.ENCODING_PCM_16BIT); // DON'T CHANGE!
		this.setMinBufferSize(AudioTrack.getMinBufferSize(
			getSampleRate(), getChannels(), getEncoding()));

		if (this.getMinBufferSize() == AudioTrack.ERROR_BAD_VALUE ||
			this.getMinBufferSize() == AudioTrack.ERROR)
		{
			throw new IOException(
				"Unable to determine the MinBufferSize for AudioTrack!");
		}

		this.setBufferSize(new Preferences(context)
			.getPcmBufferSize(getSampleRate()));
		new Utils(context).log("PCM OUT buffer size is %s.", this.getBufferSize());

		output = new WrappedAudioTrack(getAudioSource(), getSampleRate(),
			getChannels(), getEncoding(), getBufferSize(),
			AudioTrack.MODE_STREAM);

		if (output.getState() != AudioTrack.STATE_INITIALIZED)
		{
			dispose();
			throw new IOException(
				"Unable to initialize an AudioTrack instance!");
		}
	}

	@Override
	public int write(short[] buffer, int offset, int count)
	{
        if (count == 0) return 0;

//        if(MainActivity.rec_on){
//        	char[] b = new char[buffer.length*2];
//        	for(int i =0 ;i<buffer.length ; i++){
//        		b[2*i]=(char) (buffer[i]&255);
//        		b[2*i+1]=(char) (buffer[i]>>8);
//        	}
//        	try {
//				MainActivity.currRecBW.write(b, offset*2, count*2);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        	MainActivity.rec_size += count*2;
//        	return count;
//        }else{
        	return output.write(buffer, offset, count);
//        }
	}

	@Override
	public void flush()
	{
		output.flush();
	}

	@Override
	public void start()
	{
		output.play();

		// Stuff the internal PCM buffer with empty data
		final byte[] buffer = new byte[getBufferSize()];
		Arrays.fill(buffer, (byte)0);
		output.write(buffer, 0, buffer.length);
	}

	@Override
	public void stop()
	{
		output.stop();
	}

	@Override
	public void dispose()
	{
		if (output != null)
		{
			output.release();
			output = null;
		}
	}

	private final class WrappedAudioTrack extends AudioTrack
	{
		public WrappedAudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode)
			throws IllegalArgumentException
		{
			super(streamType, sampleRateInHz, channelConfig, audioFormat,
				bufferSizeInBytes, mode);

			new Utils(context).log("PCM OUT native frame count is %s.",
				getNativeFrameCount());
		}
	}
}
