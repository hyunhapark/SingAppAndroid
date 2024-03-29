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

package com.rameon.sing;

/**
 * Digital audio effects.
 * */
public enum DAFX
{
	Robotize,
	Transpose,
	Detune,
	Hoarseness;
	
	private static final DAFX[] dafxValues = DAFX.values();

	public static int count()
	{
		return dafxValues.length;
	}

	public static DAFX valueOf(int dafxIndex)
	{
		return dafxValues[dafxIndex];
	}
}
