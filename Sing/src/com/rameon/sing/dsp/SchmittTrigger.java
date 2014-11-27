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

package com.rameon.sing.dsp;

public final class SchmittTrigger
{
    private boolean state;
    private float value;

    private final float lowThreshold;
    private final float highThreshold;

    public SchmittTrigger(boolean state, float value, float lowThreshold, float highThreshold)
    {
        this.state = state;
        this.value = value;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }

    public boolean state(float newValue)
    {
        if((newValue > value) && (newValue > highThreshold))
        {
            state = true;
        }
        else if((newValue < value) && (newValue < lowThreshold))
        {
            state = false;
        }

        value = newValue;

        return state;
    }
}
