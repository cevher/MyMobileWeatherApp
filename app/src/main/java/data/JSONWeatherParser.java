package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.Utils;
import model.Place;
import model.Weather;

public class JSONWeatherParser {

    public static Weather getWeather(String data) throws JSONException {
        //create JSONObject from data
        Weather weather = new Weather();

        JSONObject jsonObject = new JSONObject(data);

        //parsing place data
        Place place = new Place();
        JSONObject coorObj = Utils.getObject("coord", jsonObject);
        place.setLat(Utils.getFloat("lat", coorObj));
        place.setLon(Utils.getFloat("lon", coorObj));

        // Parsing other data of place
        JSONObject sysObj = Utils.getObject("sys", jsonObject);
        place.setCountry(Utils.getString("country", sysObj));
        place.setLastupdate(Utils.getInt("dt", jsonObject));
        place.setSunrise(Utils.getInt("sunrise", sysObj));
        place.setSunset(Utils.getInt("sunset", sysObj));
        place.setCity(Utils.getString("name", jsonObject));
        weather.place = place;

        // Get weather data as JSONARRAY /// array has indexes
        JSONArray jsonArray = jsonObject.getJSONArray("weather");
        JSONObject jsonWeather = jsonArray.getJSONObject(0);
        weather.currentCondition.setWeatherId(Utils.getInt("id", jsonWeather));
        weather.currentCondition.setDescription(Utils.getString("description", jsonWeather));
        weather.currentCondition.setCondition(Utils.getString("main", jsonWeather));
        weather.currentCondition.setIcon(Utils.getString("icon", jsonWeather));

        JSONObject mainObj = Utils.getObject("main", jsonObject);
        weather.currentCondition.setHumidity(Utils.getInt("humidity", mainObj));
        weather.currentCondition.setPressure(Utils.getInt("pressure", mainObj));
        weather.currentCondition.setMinTemp(Utils.getFloat("temp_min", mainObj));
        weather.currentCondition.setMaxTemp(Utils.getFloat("temp_max", mainObj));
        weather.currentCondition.setTemperature(Utils.getDouble("temp", mainObj));
        /// WIND DATA
        JSONObject windObj = Utils.getObject("wind", jsonObject);
        weather.wind.setSpeed(Utils.getFloat("speed", windObj));
        weather.wind.setDeg(Utils.getFloat("deg", windObj));

        // CLOUD DATA
        JSONObject cloudObj = Utils.getObject("clouds", jsonObject);
        weather.clouds.setPrecipitation(Utils.getInt("all", cloudObj));


        return weather;

    }
}
