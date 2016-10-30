package com.oscarsalguero.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to parse weather data
 * Created by RacZo on 10/30/16.
 */

public class WeatherDataParser {

    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        JSONArray list = new JSONObject(weatherJsonStr).getJSONArray("list");
        JSONObject day = (JSONObject) list.get(dayIndex);
        return day.getJSONObject("temp").getDouble("max");
    }

}
