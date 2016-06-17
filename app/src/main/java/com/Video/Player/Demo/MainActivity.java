package com.Video.Player.Demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    // minimum video view width
    static final int MIN_WIDTH = 100;
    // Root view's LayoutParams
    private FrameLayout.LayoutParams mRootParam;
    // Custom Video View
    private org.videolan.libvlc.media.VideoView mVodView;
    // detector to pinch zoom in/out
    private ScaleGestureDetector mScaleGestureDetector;
    // detector to single tab
    private GestureDetector mGestureDetector;
    private VerticalSeekBar Brighness = null;
    private LinearLayout llvideo;
    private VerticalSeekBar volumeSeekbar;

    private AudioManager audioManager = null;
    private LinearLayout llbar,llVolume,llBrightness;
    private FrameLayout fmview;
    private float x1, x2, y1, y2, dx, dy;
    private int pointerCount;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setBrightness();
        initControls();
        mRootParam = (FrameLayout.LayoutParams) ((View) findViewById(R.id.root_view)).getLayoutParams();
        mVodView = (org.videolan.libvlc.media.VideoView) findViewById(R.id.vodView1);


        llbar=(LinearLayout)findViewById(R.id.llbar);
        llBrightness=(LinearLayout)findViewById(R.id.llBrightness);
        llVolume=(LinearLayout)findViewById(R.id.llVolume);

        // Video Uri
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.movi);
        mVodView.setVideoURI(uri);

        startActivity(new Intent(this, VLCApplication.showTvUi() ? AudioPlayerActivity.class : MainActivity.class));

        // set up gesture listeners
        mScaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
        mGestureDetector = new GestureDetector(this, new MySimpleOnGestureListener());




        mVodView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = (event.getAction() & MotionEvent.ACTION_MASK);
                 pointerCount = event.getPointerCount();

                Brighness.setVisibility(View.VISIBLE);
                Timer timer = new Timer();
                timer.schedule(new TimerTask()
                {

                    public void run()
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Brighness.setVisibility(View.GONE);
                            }
                        });
                    }

                },5000);

                mGestureDetector.onTouchEvent(event);
                mScaleGestureDetector.onTouchEvent(event);
                return true;


            }





        });
    }

    public void brightness()
    {
        Brighness.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar)
            {
                android.provider.Settings.System.putInt(getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS,
                        progress);
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress_,
                                          boolean fromUser)
            {
                Brighness.setVisibility(View.VISIBLE);
                progress = progress_;
            }

        });
    }
    public void barVisiblity()
    {
        if(pointerCount==1)
        {
            llbar.setVisibility(View.VISIBLE);
        }
        else
        {
            llbar.setVisibility(View.GONE);
        }


    }

    private void initControls()
    {
        try
        {
            volumeSeekbar = (VerticalSeekBar)findViewById(R.id.volumebar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(VerticalSeekBar seekBar)
                {

                }

                @Override
                public void onStartTrackingTouch(VerticalSeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(VerticalSeekBar seekBar, int progress_,
                                              boolean fromUser)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress_, 0);
                }


            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void setBrightness()
    {

        Brighness.setMax(255);
        float curBrightnessValue = 0;

        try
        {
            curBrightnessValue = android.provider.Settings.System.getInt(
                    getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        int screen_brightness = (int) curBrightnessValue;
        Brighness.setProgress(screen_brightness);

        brightness();



    }
    public void  initUI(){

        Brighness = (VerticalSeekBar) findViewById(R.id.brightnessbar);
        Brighness.setVisibility(View.GONE);

    }
    @Override
    protected void onResume() {
        mVodView.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mVodView.pause();
        super.onPause();
    }

    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mVodView == null)
                return false;
//            if (mVodView.isPlaying())
//                mVodView.pause();
//            else
//                mVodView.start();
            return true;
        }



        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;


            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            llbar.setVisibility(View.GONE);
                        } else {
                            llbar.setVisibility(View.GONE);
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0)
                    {
                        barVisiblity();

                    }
                    else
                    {
                        barVisiblity();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

    }


    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private int mW, mH;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // scale our video view
            mW *= detector.getScaleFactor();
            mH *= detector.getScaleFactor();
            if (mW < MIN_WIDTH)
            { // limits width
                mW = mVodView.getWidth();
                mH = mVodView.getHeight();
            }
            Log.d("onScale", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
           // mVodView.setFixedVideoSize(mW, mH); // important
            mRootParam.gravity= Gravity.CENTER;
            mRootParam.width = mW;
            mRootParam.height = mH;
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mW = mVodView.getWidth();
            mH = mVodView.getHeight();
            Log.d("onScaleBegin", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("onScaleEnd", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
        }

    }
}
