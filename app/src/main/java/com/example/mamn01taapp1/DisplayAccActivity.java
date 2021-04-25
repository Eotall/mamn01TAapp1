package com.example.mamn01taapp1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayAccActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xText, yText, zText, orientationText;
    private View bg;
    private ImageView star;
    private SensorManager mSensorManager;
    private int red = 127;
    private int blue = 127;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_acc);

        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);

        orientationText = (TextView) findViewById(R.id.orientationText);

        bg = (View) findViewById(R.id.bg);
        star = (ImageView) findViewById(R.id.star);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            xText.setText("Value X: " + String.format("%,.2f", event.values[0]));
            yText.setText("Value Y: " + String.format("%,.2f", event.values[1]));
            zText.setText("Value Z: " + String.format("%,.2f", event.values[2]));


        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = event.values[0];
            float y = event.values[1];

            StringBuilder sb = new StringBuilder();
            if (x > 0.2) {
                sb.append(" Left");
            } else if (x < -0.2) {
                sb.append(" Right");
            }
            if (y > 0.2) {
                sb.append(" Back");
            } else if (y < -0.2) {
                sb.append(" Forward");
            }
            orientationText.setText(sb.toString());

            //calculate new background color
            double step = 255/9.81;
            //red += (x);
            //blue += (y);
            red = 255 - Math.abs((int) Math.round(x*step));
            blue = 255 - Math.abs((int) Math.round(y*step));
            bg.setBackgroundColor(Color.argb(100,red,0,blue));

            //move star
            float newX = star.getX()-x;
            float newY = star.getY()+y;
            float maxWidth = (bg.getWidth() - star.getWidth());
            float minHeight = bg.getY();
            float maxHeight = (bg.getHeight() - star.getHeight() + minHeight);
            if (newX < 0) {
                newX = 0;
            } else if (newX > maxWidth) {
                newX = maxWidth;
            }
            if (newY < minHeight) {
                newY = bg.getY();
            } else if (newY > maxHeight) {
                newY = maxHeight;
            }
            star.setX(newX);
            star.setY(newY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}