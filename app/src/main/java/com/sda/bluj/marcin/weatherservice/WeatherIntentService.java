package com.sda.bluj.marcin.weatherservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by RENT on 2017-02-28.
 */

public class WeatherIntentService extends IntentService {

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            Log.i("WeatherService", intent.getAction());
            if ("GET_WEATHER".equals(action)) {
                String city = intent.getStringExtra("CITY");
                getWeather(city);
            }
        }
    }

    private void getWeather(String city) {
        Log.i("WeatherService", "getWeather - " + city);
        Intent intent = new Intent();
        intent.setAction("WEATHER_RESPONSE");
        intent.putExtra("TEMPERATURE", 26.1);
        intent.putExtra("PRESSURE", 1018);
        intent.putExtra("MAIN", "Cloudy");
        intent.putExtra("DATE", System.currentTimeMillis());
        intent.putExtra("CITY", city);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.sendBroadcast(intent);
    }
}
