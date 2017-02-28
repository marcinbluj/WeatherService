package com.sda.bluj.marcin.weatherservice;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by RENT on 2017-02-28.
 */

public class WeatherAsyncTask extends AsyncTask<String, Integer, String> {
    public MainActivity mainActivity;
    public String city;

    public WeatherAsyncTask(String city) {
        this.city = city;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return sendPost();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "blad";
    }

    private String sendPost() throws IOException {

        OkHttpClient client = new OkHttpClient();

        String url = "http://api.openweathermap.org/data/2.5/weather?q=Wroclaw&appid=7a4a4065de2be82b93998458ee726128&units=metric";

        Request.Builder builder = new Request.Builder();
        builder.url(url);

        Request request = builder.build();
        Response response = client.newCall(request).execute();

        return response.body().string();

    }

    @Override
    protected void onPostExecute(String body) {
        super.onPostExecute(body);
        Log.i("TEST", body);

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String weatherType = jsonBody.optJSONArray("weather").optJSONObject(0).optString("main");
        String temperature = jsonBody.optJSONObject("main").optString("temp");
        String pressure = jsonBody.optJSONObject("main").optString("pressure");
        String dt = jsonBody.optString("dt");

        Weather weather = new Weather();
        weather.setDate(dt);
        weather.setPressure(pressure);
        weather.setTemperature(temperature);
        weather.setMain(weatherType);

//        mainActivity.setWeather(weather);

        Log.i("TEST", weather.getPressure());
    }
}
