package com.example.mamn01taapp1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayCompActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView compass;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    public TextView headingText;
    private Vibrator v;
    protected float[] magSensorVals;
    private static final float ALPHA = 0.25f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_comp);

        compass = (ImageView) findViewById(R.id.compassView);
        headingText = (TextView) findViewById(R.id.headingText);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Vibrator
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magSensorVals = lowPass(event.values.clone(), magSensorVals);


        if(magSensorVals != null) {
            // extract rotation from sensor
            float degree = event.values[0];

            // normalise rotation to 0-360 degrees
            while (degree >= 360) {
                degree -= 360;
            }
            while (degree < 0) {
                degree += 360;
            }

            // display rotation in textView, rounded because we don't need the precision
            headingText.setText("Heading: " + Float.toString(Math.round(degree)) + " degrees");

            // change picture based on rotation
            // also, play sound (doesn't work, app crashes)
            //MediaPlayer mp1 = MediaPlayer.create(this, R.raw.single_click);
            //MediaPlayer mp2 = MediaPlayer.create(this, R.raw.double_click);
            //MediaActionSound sound = new MediaActionSound();

            if (Math.abs(degree) <= 15f || Math.abs(degree) >= 345f) {
                compass.setImageResource(R.drawable.compass_icon_north);
                //sound.play(MediaActionSound.START_VIDEO_RECORDING);
            } else if (Math.abs(degree) <= 105f && Math.abs(degree) >= 75f) {
                compass.setImageResource(R.drawable.compass_icon_east);
                //sound.play(MediaActionSound.STOP_VIDEO_RECORDING);
            } else if (Math.abs(degree) <= 195f && Math.abs(degree) >= 165f) {
                compass.setImageResource(R.drawable.compass_icon_south);
                //sound.play(MediaActionSound.STOP_VIDEO_RECORDING);
            } else if (Math.abs(degree) <= 285f && Math.abs(degree) >= 255f) {
                compass.setImageResource(R.drawable.compass_icon_west);
                //sound.play(MediaActionSound.STOP_VIDEO_RECORDING);
            } else {
                compass.setImageResource(R.drawable.compass_icon);
            }

            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            ra.setDuration(210);
            ra.setFillAfter(true);
            compass.startAnimation(ra);
            currentDegree = -degree;
        }
        // vibrate if phone is at too steep an angle
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (Math.abs(event.values[0]) > 3 || Math.abs(event.values[1]) > 3) {
                v.vibrate(400);
                System.out.println("Nu vibrerar det!");
            }
        }
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}