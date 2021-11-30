package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

private EditText editTextCity;
public static     TextView textViewWeather;
private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=47fa671071e89616b544892a4e94a1ad&lang=ru";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      editTextCity = findViewById(R.id.editTextCity);
      textViewWeather = findViewById(R.id.textViewDescription);

    }

    public void onClickShowWeather(View view) {
     String city = editTextCity.getText().toString().trim();
     if (!city.isEmpty()){
         String url = String.format(WEATHER_URL,city);
         WeatherTask task = new WeatherTask();
         task.execute(url);
         editTextCity.setText("");
     }
     else{
         Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
     }
    }

    private static class WeatherTask extends AsyncTask<String,Void,String>{

      @Override
      protected String doInBackground(String... strings) {
          URL url = null;
          HttpURLConnection connection = null;
          StringBuilder result = new StringBuilder();
          try {
              url = new URL(strings[0]);
              connection = (HttpURLConnection)url.openConnection();
              InputStream inputStream = connection.getInputStream();
              InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
              BufferedReader reader = new BufferedReader(inputStreamReader);
              String line = reader.readLine();
              while (line !=null){
                  result.append(line);
                  line = reader.readLine();
              }
              return result.toString();
          } catch (MalformedURLException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          } finally {
              if (connection !=null){
                  connection.disconnect();
              }
          }
          return null;

      }

      @Override
      protected void onPostExecute(String s) {
          super.onPostExecute(s);
          try {
              JSONObject jsonObject = new JSONObject(s);
              String city = jsonObject.getString("name");
              String temp = jsonObject.getJSONObject("main").getString("temp");
              String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
              String weather = String.format("%s\nТемпература: %s\nНа улице : %s",city,temp,description);
              textViewWeather.setText(weather);
          } catch (JSONException e) {
              e.printStackTrace();
          }

      }
  }

}
