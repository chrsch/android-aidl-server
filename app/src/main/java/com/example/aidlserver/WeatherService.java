package com.example.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class WeatherService extends Service {

    private static final String TAG = "WeatherService";

    public WeatherService() {
        Log.d(TAG, "WeatherService() called");
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG, "WeatherService::onBind called");
        return new IWeather.Stub() {
            @Override
            public WeatherResult getWeather(IWeatherCallback callback) throws RemoteException {
                Log.d(TAG, "getWeather called");
                long requestTime = new Date().getTime();
                final boolean[] success = {false};  // Use a final array to hold the boolean

                new Thread(() -> {
                    try {
                        String jsonResponse = fetchWeatherData();
                        success[0] = true;  // Update the success status

                        if (callback != null) {
                            callback.onSuccess(jsonResponse);  // Send back the full JSON response
                        }

                    } catch (Exception e) {
                        if (callback != null) {
                            try {
                                callback.onException(e.getMessage());
                            } catch (RemoteException remoteException) {
                                remoteException.printStackTrace();
                            }
                        }
                    }
                }).start();

                // Create and return a WeatherResult object
                WeatherResult result = new WeatherResult();
                result.isSuccess = success[0];  // Access the boolean value from the array
                result.datetime = requestTime;
                return result;
            }
        };
    }

    private String fetchWeatherData() throws Exception {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=48.13743&longitude=11.57549&hourly=temperature_2m&timezone=Europe%2FBerlin&forecast_days=1";
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            in.close();
            return response.toString();
        } finally {
            urlConnection.disconnect();
        }
    }
}
