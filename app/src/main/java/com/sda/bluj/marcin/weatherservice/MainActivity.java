package com.sda.bluj.marcin.weatherservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.icon)
    ImageView mIcon;

    @BindView(R.id.main)
    TextView mMain;

    @BindView(R.id.date)
    TextView mDate;

    @BindView(R.id.temperature)
    TextView mTemperature;

    @BindView(R.id.pressure)
    TextView mPressure;

    @BindView(R.id.city)
    TextView mCity;

    @BindView(R.id.show_weather_button)
    Button button;

    @BindView(R.id.city_edit_text)
    EditText inputText;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double temperature = intent.getDoubleExtra("TEMPERATURE", 0.0d);
            int pressure = intent.getIntExtra("PRESSURE", 0);
            String city = intent.getStringExtra("CITY");
            long date = intent.getLongExtra("DATE", 0L);
            String icon = intent.getStringExtra("ICON");
            String main = intent.getStringExtra("MAIN");

            mCity.setText(city);
            mMain.setText(String.format("Main: %s", main));
            mTemperature.setText("Temperature: " + temperature + " C");
            mPressure.setText("Pressure: " + pressure + " hPa");
            mDate.setText(String.format("Date: %s", convertDate(date)));

            Picasso.with(MainActivity.this)
                    .load(icon)
                    .fit()
                    .into(MainActivity.this.mIcon);
        }
    };

    private String convertDate(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("WEATHER_RESPONSE");

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    @OnClick(R.id.show_weather_button)
    public void onWeatherButtonClick(View view) {

        String city = inputText.getText().toString();

        if (!city.isEmpty()) {
            Intent intent = new Intent(this, WeatherIntentService.class);
            intent.setAction("GET_WEATHER");
            intent.putExtra("CITY", city);
            startService(intent);
        }

    }

    public void alarmStartStop (View view) {
        switch (view.getId()) {
            case R.id.start:
                startAlarm();
                break;
            case R.id.stop:
                stopAlarm();
                break;
        }
    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = createServiceIntent();
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long startTime = System.currentTimeMillis();
        long interval = 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
    }

    private void stopAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = createServiceIntent();
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private Intent createServiceIntent() {

        Intent intent = new Intent(this, WeatherIntentService.class);
        String city = inputText.getText().toString();

        if (!city.isEmpty()) {
            intent.setAction("GET_WEATHER");
            intent.putExtra("CITY", city);
            startService(intent);
        }

        return intent;
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}