package me.selinali.drizzle.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherResponse {
    private static final Map<String, String> BLURBS = new HashMap<>();
    static {
        BLURBS.put("01d", "Clear");
        BLURBS.put("01n", "Clear");
        BLURBS.put("02d", "Few Clouds");
        BLURBS.put("02n", "Few Clouds");
        BLURBS.put("03d", "Scattered Clouds");
        BLURBS.put("03n", "Scattered Clouds");
        BLURBS.put("04d", "Broken Clouds");
        BLURBS.put("04n", "Broken Clouds");
        BLURBS.put("09d", "Shower Rain");
        BLURBS.put("09n", "Shower Rain");
        BLURBS.put("10d", "Rain");
        BLURBS.put("10n", "Rain");
        BLURBS.put("11d", "Thunderstorm");
        BLURBS.put("11n", "Thunderstorm");
        BLURBS.put("13d", "Snow");
        BLURBS.put("13n", "Snow");
        BLURBS.put("50d", "Mist");
        BLURBS.put("50n", "Mist");
    }


    public class Main {
        public Double temp;
    }

    public class Weather {
        public String icon;
    }

    public List<Weather> weather = new ArrayList<>();
    public Main main;

    public String getBlurb() {
        return BLURBS.get(weather.get(0).icon);
    }

    public double getTemperature() {
        return main.temp;
    }
}
