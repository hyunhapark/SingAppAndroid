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

import static com.rameon.sing.dsp.Math.mean;
import android.content.Context;

import com.rameon.sing.Preferences;
import com.rameon.sing.dsp.LuenbergerObserver;


public final class OffsetProcessor
{
    private final boolean isEnabled;

    private final float[] offsetObserverGain = new float[] {0.025F, 0F};

    private final LuenbergerObserver offsetObserver;

    public OffsetProcessor(Context context)
    {
        this(new Preferences(context).isCorrectOffsetOn());
    }

    public OffsetProcessor(boolean enable)
    {
        isEnabled = enable;

        offsetObserver = new LuenbergerObserver(0, 0, offsetObserverGain);
    }

    public void processFrame(short[] frame)
    {
        if (!isEnabled) return;

        short currentOffset = mean(frame, 0, frame.length);
        currentOffset = (short)offsetObserver.smooth(currentOffset);

        for (int i = 0; i < frame.length; i++)
        {
            frame[i] -= currentOffset;
        }
    }
}
