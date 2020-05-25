package com.example.mymobileweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import Util.Utils;
import data.CityPreference;
import data.JSONWeatherParser;
import data.WeatherHttpClient;
import model.Weather;

public class MainActivity extends AppCompatActivity {

    private TextView cityName;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;
    private Button changeCityButton;


    // Global Weather object for async task and rendering
    Weather weather = new Weather();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityText);
        temp = findViewById(R.id.tempText);
        iconView = findViewById(R.id.thumbnailIcon);
        description = findViewById(R.id.cloudText);
        humidity = findViewById(R.id.humidityText);
        pressure = findViewById(R.id.pressureText);
        wind = findViewById(R.id.windText);
        sunrise = findViewById(R.id.riseText);
        sunset = findViewById(R.id.setText);
        updated = findViewById(R.id.updateText);
        CityPreference cityPreference = new CityPreference(MainActivity.this);

        renderWeatherData(cityPreference.getCity());

        changeCityButton = findViewById(R.id.changeCityButton);
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });


    }


    // render the parsed weather data
    public void renderWeatherData(String city){
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city + "&units=metric&APPID=504692187968b9deebb20488775a0d5d" }); // units=metric to chose data type

    }

// ASync task to dowload image
private class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{


    @Override
    protected Bitmap doInBackground(String... params) {

            return downloadImage(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
            iconView.setImageBitmap(bitmap);
    }

    private Bitmap downloadImage(String code) {
     HttpURLConnection connection = null;

        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) (new URL(Utils.ICON_URL + code + ".png")).openConnection();
            Log.v("Url", Utils.ICON_URL + code + ".png");
            connection.setDoInput(true);
            connection.connect();
            inputStream = connection.getInputStream();
            BufferedInputStream  bufferedInputStream  = new BufferedInputStream(inputStream);
            Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);
            Log.v("Image", "Downloaded");
            connection.disconnect();
            inputStream.close();
            return bmp;

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Image", "Cannot Downloaded");
        connection.disconnect();

      return null;
    }
}






    //asyncronous implementing of http & parsing in the background task
    private class WeatherTask extends AsyncTask<String, Void, Weather> {


        @Override
        protected Weather doInBackground(String... params) {
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

           // the first parameter in the params array which is CITY
            try {
                weather = JSONWeatherParser.getWeather(data);
                weather.iconData = weather.currentCondition.getIcon();
                Log.v("Icon Data:", weather.iconData);
                new DownloadImageAsyncTask().execute(weather.iconData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return weather;
        }


        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
            // setting UI elements with the data get from web
            DateFormat df = DateFormat.getTimeInstance();
            String sunriseDate = df.format(new Date(weather.place.getSunrise()));
            String sunsetDate = df.format(new Date(weather.place.getSunset()));
            String updateDate = df.format(new Date(weather.place.getLastupdate()));
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat = decimalFormat.format(weather.currentCondition.getTemperature());


            cityName.setText(weather.place.getCity() + ", " + weather.place.getCountry());
            temp.setText("" + tempFormat + "C");
            humidity.setText("Humidity: " + weather.currentCondition.getHumidity() + "%" );
            pressure.setText("Pressure: " +weather.currentCondition.getPressure() + "hPa");
            wind.setText("Wind: " + weather.wind.getSpeed()+ "m/sec" );
            sunrise.setText("Sunrise: "+ sunriseDate);
            sunset.setText("Sunset: " + sunsetDate);
            updated.setText("Last Update: " + updateDate);
            description.setText("Condition: " + weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescription() + ")");
        }
    }

    private void showInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Change City");
        final EditText cityInput = new EditText(MainActivity.this);
        cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cityInput.setHint("Antalya,TUR");
        builder.setView(cityInput);
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CityPreference cityPreference = new CityPreference(MainActivity.this);
                cityPreference.setCity(cityInput.getText().toString());
                String newCity = cityPreference.getCity();
                renderWeatherData(newCity);

            }
        });
        builder.show();
    }
}
