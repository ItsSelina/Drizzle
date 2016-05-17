package me.selinali.drizzle.api;

import android.location.Location;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

public class WeatherApi {
    public interface Endpoint {
        @GET("weather")
        Observable<WeatherResponse> getWeather(@Query("lat") double latitude,
                                               @Query("lon") double longitude,
                                               @Query("units") String units,
                                               @Query("appid") String appId);
    }

    public static WeatherApi sInstance;

    public static WeatherApi instance() {
        if (sInstance == null) {
            sInstance = new WeatherApi();
        }
        return sInstance;
    }

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String APP_ID = "4e0c5ddeedd010818e1ab8b9d7ca2ec0";

    private final Endpoint mEndpoint;

    private WeatherApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        mEndpoint = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(Endpoint.class);
    }

    public Single<WeatherResponse> getWeather(Location location) {
        return mEndpoint.getWeather(location.getLatitude(), location.getLongitude(),
                "metric", APP_ID)
                .toSingle();
    }
}
