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
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

// CameraActivity
@SuppressWarnings("deprecation")
public class CameraActivity extends Activity
{
    static final private String TAG = "CameraActivity";
    static final private String ID = "id";

    static final private int BUTTON_SIZE = 56;
    static final private int MARGIN = 56;

    private int id;
    private android.hardware.Camera camera;
    private CameraPreview preview;
    private HistogramView histogram;
    private ImageButton button;

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

        button = new ImageButton(this);
        button.setImageResource(R.drawable.ic_action_switch_camera);
        button.setBackgroundResource(R.drawable.ic_button_background);
        parent.addView(button);

        FrameLayout.LayoutParams params =
            (FrameLayout.LayoutParams) button.getLayoutParams();
        params.height = BUTTON_SIZE;
        params.width = BUTTON_SIZE;
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        params.leftMargin = MARGIN;
        params.rightMargin = MARGIN;
        button.setLayoutParams(params);

        button.setOnClickListener(v ->
            {
                int cameras = android.hardware.Camera.getNumberOfCameras();
                id = (id + 1) % cameras;
                recreate();
            });

        if (savedInstanceState != null)
            id = savedInstanceState.getInt(ID);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Create an instance of Camera
        camera = android.hardware.Camera.open(id);

        Configuration config = getResources().getConfiguration();

        switch (config.orientation)
        {
        case Configuration.ORIENTATION_PORTRAIT:
            // Orient the camera
            camera.setDisplayOrientation(90);
            break;
        }

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
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
            catch (Exception e)
            {
            }
        }
    }

    // onSaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ID, id);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN)
        {
            if (histogram.getVisibility() == View.VISIBLE)
                histogram.setVisibility(View.INVISIBLE);

            else
                histogram.setVisibility(View.VISIBLE);
        }

        return true;
    }
}
