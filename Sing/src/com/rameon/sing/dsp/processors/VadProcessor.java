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

package com.rameon.sing.dsp.processors;

import static com.rameon.sing.dsp.Math.ceil;
import static com.rameon.sing.dsp.Math.min;
import static com.rameon.sing.dsp.Math.rms;
import static com.rameon.sing.dsp.Math.rms2dbfs;
import static com.rameon.sing.dsp.Math.round;
import android.content.Context;

import com.rameon.sing.Preferences;
import com.rameon.sing.Utils;
import com.rameon.sing.dsp.LuenbergerObserver;
import com.rameon.sing.dsp.SchmittTrigger;


public final class VadProcessor
{
    private final int sampleRate;

	private final float windowTimeInterval = 20e-3F;
	private final int windowSize;

	private final float[] energyObserverGain = new float[] {0.3F, 0.02F};

	private final LuenbergerObserver energyObserver;
	private final SchmittTrigger trigger;

    private final float hangoverMaxDuration;
    private float hangoverDuration;

    private final boolean isEnabled;

    // Special variables for debugging
    private Utils utils = null;
    private boolean lastState = false;

    public VadProcessor(int sampleRate, Context context)
    {
        this(sampleRate,
            new Preferences(context).getAutoMuteHighThreshold(),
            new Preferences(context).getAutoMuteLowThreshold(),
            new Preferences(context).getAutoMuteHangover(),
            new Preferences(context).isAutoMuteOn());

        if(new Preferences(context).isLoggingOn())
            utils = new Utils(context);
    }

	public VadProcessor(int sampleRate, int lowThreshold, int highThreshold, int hangover, boolean enable)
	{
        this.sampleRate = sampleRate;

		windowSize = round(sampleRate * windowTimeInterval);
        if (utils != null) utils.log("VAD desired window size is %s.", windowSize);

		final float initialDbfs = (lowThreshold + highThreshold) / 2F;

		energyObserver = new LuenbergerObserver(initialDbfs, 0, energyObserverGain);
		trigger = new SchmittTrigger(false, initialDbfs, lowThreshold, highThreshold);

        hangoverMaxDuration = hangover;
        hangoverDuration = 0;

        isEnabled = enable;
	}

	public void processFrame(short[] frame)
	{
        if(!isEnabled) return;

		final int windowCount = frame.length / windowSize;
		final int adaptedWindowSize = windowCount > 0
                ? (int)ceil((float)frame.length / (float)windowCount) // round up division result, if numbers are odd
                : frame.length; // if the window is smaller than frame, so make it just bigger
        final float windowDuration = (float)adaptedWindowSize / (float)sampleRate;

		for (int i = 0; i < windowCount; i++)
		{
			final int windowOffset = i * adaptedWindowSize;
			processFrameInternal(frame, windowOffset, adaptedWindowSize, windowDuration);
		}
	}

	private void processFrameInternal(short[] frame, int offset, int length, float windowDuration)
	{
		float currentRms = rms(frame, offset, length);
		float currentDbfs = rms2dbfs(currentRms, 1e-10F, 1F);

		currentDbfs = energyObserver.smooth(currentDbfs);
		boolean currentState = trigger.state(currentDbfs);

        if(hangoverMaxDuration > 0)
        {
            if(!currentState)
            {
                hangoverDuration = min(hangoverMaxDuration, hangoverDuration + windowDuration);
                currentState = hangoverDuration < hangoverMaxDuration;
            }
            else
            {
                hangoverDuration = 0;
            }
        }

		if(!currentState)
        {
            for (int i = offset; i < offset + length; i++)
            {
                frame[i] = 0;
            }
        }

        // Log state changes
        if (utils != null && lastState != currentState)
        {
            if (currentState) utils.log("Voice activity detected.");
            else utils.log("Voice inactivity detected.");
            lastState = currentState;
        }
	}
}
