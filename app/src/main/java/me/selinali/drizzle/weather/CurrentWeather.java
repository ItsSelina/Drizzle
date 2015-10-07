package me.selinali.drizzle.weather;


public class CurrentWeather {
    private String main;

    public String getMain() {
        return main.toLowerCase();
    }

    public void setMain(String main) {
        this.main = main;
    }
}