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

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

// CameraActivity
@SuppressWarnings("deprecation")
public class CameraActivity extends Activity
{
    static final private String TAG = "CameraActivity";

    static final private String ID = "id";

    private int id;
    private Camera camera;
    private CameraPreview preview;
    private HistogramView histogram;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create our Preview view and set it as the content of our
        // activity.
        preview = new CameraPreview(this);
        setContentView(preview);
        histogram = new HistogramView(this);
        ViewGroup parent = (ViewGroup) preview.getParent();
        parent.addView(histogram);
	preview.setHistogramView(histogram);

        if (savedInstanceState != null)
            id = savedInstanceState.getInt(ID);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Create an instance of Camera
        camera = Camera.open(id);

	// Set preview camera
	preview.setCamera(camera);

	preview
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                   View.SYSTEM_UI_FLAG_LOW_PROFILE |
                                   View.SYSTEM_UI_FLAG_FULLSCREEN);
        try
        {
            camera.startPreview();
        }

        catch (Exception e) {}
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.

        if (camera != null)
        {
	    try
	    {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	    }

	    catch (Exception e) {}
	}
    }

    // onSaveInstanceState
    @Override
    public void onSaveInstanceState (Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ID, id);
    }

    public boolean onTouchEvent (MotionEvent event)
    {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
            int width = histogram.getWidth();
            float x = event.getX();

            if (x < width / 2)
            {
                int cameras = camera.getNumberOfCameras();
                id = (id + 1) % cameras;
                recreate();
            }

            else
            {
                if (histogram.getVisibility() == View.VISIBLE)
                    histogram.setVisibility(View.INVISIBLE);

                else
                    histogram.setVisibility(View.VISIBLE);
            }
        }

        return true;
    }
}
