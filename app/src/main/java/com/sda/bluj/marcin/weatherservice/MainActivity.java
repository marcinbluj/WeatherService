package com.sda.bluj.marcin.weatherservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    Weather weather;

    WeatherAsyncTask weatherAsyncTask;

    @BindView(R.id.main)
    TextView mWeatherType;

    @BindView(R.id.date)
    TextView mDate;

    @BindView(R.id.temperature)
    TextView mTemperature;

    @BindView(R.id.pressure)
    TextView mPressure;

    @BindView(R.id.city)
    TextView mCity;

    @BindView(R.id.random_button)
    Button button;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double temperature = intent.getDoubleExtra("TEMPERATURE", 0.0d);
            int pressure = intent.getIntExtra("PRESSURE", 0);
            String city = intent.getStringExtra("CITY");
            long date = intent.getLongExtra("DATE", 0L);

            mTemperature.setText(temperature+"");
            mPressure.setText(pressure+"");
            mCity.setText(city);
            mDate.setText(convertDate(date));
        }
    };

    private String convertDate(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeatherIntentService.class);
                intent.setAction("GET_WEATHER");
                intent.putExtra("CITY", "Wroclaw");

                startService(intent);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("WEATHER_RESPONSE");

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

//    public void displayData(View view) {
//        if (view instanceof Button) {
//            Button button = (Button) view;
//            getData(button);
//
//        }
//    }
//
//    private void getData(Button button) {
//        WeatherAsyncTask weatherAsyncTask = new WeatherAsyncTask(button.getText().toString());
//        weatherAsyncTask.setMainActivity(this);
//        weatherAsyncTask.execute();
//    }
//
//    public void setWeather(Weather weather) {
//        this.weather = weather;
//    }
}
