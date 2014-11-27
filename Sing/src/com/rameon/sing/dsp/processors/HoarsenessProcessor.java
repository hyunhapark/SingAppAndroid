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

import static com.rameon.sing.dsp.Math.PI;
import static com.rameon.sing.dsp.Math.abs;
import static com.rameon.sing.dsp.Math.imag;
import static com.rameon.sing.dsp.Math.real;

public final class HoarsenessProcessor
{
	public static void processFrame(float[] frame)
	{
		final int fftSize = frame.length / 2;
		float re, im, abs, phase;

		for (int i = 1; i < fftSize; i++)
		{
			// Get source Re and Im parts
			re = frame[2 * i];
			im = frame[2 * i + 1];
			abs = abs(re, im);

			// Compute random phase
//			phase = random(-PI, PI);
			phase = (float) (Math.random()*2*PI-PI);

			// Compute destination Re and Im parts
			frame[2 * i] = real(abs, phase);
			frame[2 * i + 1] = imag(abs, phase);
		}
	}
}
