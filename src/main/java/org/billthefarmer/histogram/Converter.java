////////////////////////////////////////////////////////////////////////////////
//
//  Histogram - An Android Histogram app.
//
//  Copyright (C) 2014	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.histogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.ScriptIntrinsicHistogram;
import android.renderscript.Type;
import android.util.Log;

public class Converter
{
    static final private String TAG = "Converter";

    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB si;
    private ScriptIntrinsicHistogram hi;
    private Allocation yuvIn;
    private Allocation rgbIn;
    private Allocation rgbOut;
    private Allocation intOut;

    private byte[] pixels;
    private int[] hist;

    public Converter(Context context)
    {
	rs = RenderScript.create(context);
	si = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
	hi = ScriptIntrinsicHistogram.create(rs, Element.U8_4(rs));
    }

    public byte[] convertToRGB(byte[] yuv, int width, int height)
    {
	int size = (width * height * 4);

	if (yuvIn == null || pixels.length != size)
	{
	    Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
		.setX(yuv.length);
	    yuvIn = Allocation.createTyped(rs, yuvType.create());

	    Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
		.setX(width)
		.setY(height);
	    rgbOut = Allocation.createTyped(rs, rgbaType.create());
	    pixels = new byte[size];
	}

	yuvIn.copyFrom(yuv);
	si.setInput(yuvIn);
	si.forEach(rgbOut);
	rgbOut.copyTo(pixels);

	return pixels;
    }

    public int[] histogram(byte[] rgb, int width, int height)
    {
	int size = (width * height * 4);

	if (rgbIn == null || rgbIn.getBytesSize() != size)
	{
	    Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
		.setX(width)
		.setY(height);
	    rgbIn = Allocation.createTyped(rs, rgbaType.create());

	    Type.Builder uintType = new Type.Builder(rs, Element.U32_3(rs))
		.setX(256);
	    intOut = Allocation.createTyped(rs, uintType.create());
            hist = new int[256 * 4];
        }

	rgbIn.copyFrom(rgb);
        hi.setOutput(intOut);
        hi.forEach(rgbIn);
        intOut.copyTo(hist);

        return hist;
    }
}
