package data;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Util.Utils;

public class WeatherHttpClient  {

    public String getWeatherData(String place){
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            // URL url = new URL(Utils.BASE_URL + place);  usesClearText=true hata bulundu
             // System.out.println("URL: "+ url);
            connection = (HttpURLConnection) (new URL(Utils.BASE_URL + place)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            // Read Response from URL
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            connection.disconnect();
            // Log.v("Buffered Data: ", stringBuffer.toString());
            return stringBuffer.toString();

        } catch (IOException e){
            e.printStackTrace();
        }
    return null;
    }
}
