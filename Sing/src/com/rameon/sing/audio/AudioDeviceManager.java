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

package com.rameon.sing.audio;

import java.io.IOException;

import android.content.Context;

import com.rameon.sing.io.AudioDevice;
import com.rameon.sing.io.pcm.PcmInDevice;
import com.rameon.sing.io.pcm.PcmOutDevice;

/**
 * Provides audio device management routines.
 * Can be mocked for testing purposes.
 */
public class AudioDeviceManager
{
    private final Context context;

    public AudioDeviceManager(Context context)
    {
        this.context = context;
    }

    public AudioDevice getInputDevice(HeadsetMode mode) throws IOException
    {
        // TEST: Read input signal from file instead of mic device
        // return new FileInDevice(context, "voicesmith_input.raw");

        return new PcmInDevice(context, mode);
    }

    public AudioDevice getOutputDevice(HeadsetMode mode) throws IOException
    {
        // TEST: Write output signal to file instead of output jack
        // (also enable WRITE_EXTERNAL_STORAGE permission in the manifest file)
        // return new FileOutDevice(context, "voicesmith_output.raw");

        return new PcmOutDevice(context, mode);
    }
}
