package com.sda.bluj.marcin.weatherservice;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by RENT on 2017-02-28.
 */

public class WeatherIntentService extends IntentService {

    SharedPreferences preferences;

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if ("GET_WEATHER".equals(action)) {
                String city = intent.getStringExtra("CITY");
                try {
                    getWeather(city);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getWeather(String city) throws IOException, JSONException {

        JSONObject body = sendRequest(city);
//        int cod = body.optInt("cod");
        sendData(city, body);
    }

    private void sendData(String city, JSONObject body) {

        Weather weather = getWeatherFromJson(body);
        weather.setCity(city);

        Log.i("WeatherService", "city - " + weather.getCity());
        Intent intent = new Intent();
        intent.setAction("WEATHER_RESPONSE");

        if (weather.isState()) {
            intent.putExtra("TEMPERATURE", weather.getTemperature());
            intent.putExtra("PRESSURE", weather.getPressure());
            intent.putExtra("MAIN", weather.getMain());
            intent.putExtra("DATE", weather.getDate());
            intent.putExtra("ICON", weather.getIcon());
            intent.putExtra("CITY", weather.getCity());

            preferences = getSharedPreferences("weather_data", MODE_PRIVATE);
            preferences.edit()
                    .putFloat("TEMPERATURE", (float) weather.getTemperature())
                    .putInt("PRESSURE", weather.getPressure())
                    .putString("MAIN", weather.getMain())
                    .putLong("DATE", weather.getDate())
                    .putString("ICON", weather.getIcon())
                    .putString("CITY", weather.getCity())
                    .apply();
        } else {
            preferences = getSharedPreferences("weather_data", MODE_PRIVATE);
            float prefTemperature = preferences.getFloat("TEMPERATURE", 0.0f);
            int prefPressure = preferences.getInt("PRESSURE", 0);
            String prefMain = preferences.getString("MAIN", "");
            long prefDate = preferences.getLong("DATE", 0L);
            String prefIcon = preferences.getString("ICON", "none");
            String prefCity = preferences.getString("CITY", "incorrect");

            intent.putExtra("TEMPERATURE", (double) prefTemperature);
            intent.putExtra("PRESSURE", prefPressure);
            intent.putExtra("MAIN", prefMain);
            intent.putExtra("DATE", prefDate);
            intent.putExtra("ICON", prefIcon);
            intent.putExtra("CITY", prefCity);
        }

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.sendBroadcast(intent);
    }

    private JSONObject sendRequest(String city) {
        Request.Builder builder = new Request.Builder();
        builder.url("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=7a4a4065de2be82b93998458ee726128");
        builder.get();

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();

        Response response;
        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            Log.i("WEATHER", "IOException");
            return new JSONObject();
        } catch (JSONException e) {
            Log.i("WEATHER", "JSONException");
            return new JSONObject();
        }
    }

    private Weather getWeatherFromJson(JSONObject body) {
        Log.i("WEATHER", body.toString());
        Weather weather = new Weather();
        if (body.optJSONArray("weather") != null && body.optJSONArray("weather").length() != 0) {
            weather.setMain(body
                    .optJSONArray("weather").optJSONObject(0).optString("description"));
            weather.setIcon("http://openweathermap.org/img/w/" +
                    body.optJSONArray("weather").optJSONObject(0).optString("icon") + ".png");
            weather.setTemperature(body.optJSONObject("main").optDouble("temp"));
            weather.setPressure(body.optJSONObject("main").optInt("pressure"));
            weather.setDate(body.optLong("dt") * 1000);
            weather.setState(true);
        } else {
            weather.setState(false);
        }
        return weather;
    }
}
