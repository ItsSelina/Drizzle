package me.selinali.drizzle.ui;

import android.app.WallpaperManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.selinali.drizzle.CurrentLocation;
import me.selinali.drizzle.R;
import me.selinali.drizzle.weather.CurrentWeather;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private CurrentWeather currentWeather;
    private String weatherName;

    private GoogleApiClient googleApiClient;
    private CurrentLocation currentLocation;
    private Location location;

    private double latitude;
    private double longitude;

    @Bind(R.id.current_weather_text)
    TextView currentWeatherText;

    @Bind(R.id.set_background_button)
    Button setBackgroundButton;

    @Bind(R.id.location_text)
    TextView locationText;

    @Bind(R.id.get_location)
    Button getLocationButton;

    @Bind(R.id.current_location_text)
    TextView currentLocationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        buildGoogleApiClient();

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocation();
            }
        });

        latitude = getLatitude();
        longitude = getLongitude();

        getForecast(latitude, longitude);

        try {
            currentLocation = getCurrentLocation();
            currentLocationText.setText(currentLocation.getCity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setBackgroundButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

                weatherName = currentWeather.getMain();

                try {
                    wallpaperManager.setResource(getResources().getIdentifier(weatherName, "drawable", getPackageName())); // This returns the same integer value as R.drawable.weatherName
                    Snackbar.make(arg0, "Wallpaper set successfully.", Snackbar.LENGTH_LONG).show();
                } catch (IOException e) {
                    errorMessage();
                }
            }
        });
    }

    private void getForecast(double latitude, double longitude) {
        //String forecastUrl = "http://api.openweathermap.org/data/2.5/weather?lat=43.7765881&lon=-79.3277382&appid=bd82977b86bf27fb59a04b61b657fb6f";
        String forecastUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=4e0c5ddeedd010818e1ab8b9d7ca2ec0";

        if (networkIsAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                    errorMessage();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            currentWeather = getWeather(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    currentWeatherText.setText(currentWeather.getMain());
                                }
                            });
                        } else {
                            errorMessage();
                        }
                    } catch (IOException | JSONException e) {
                        errorMessage();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Network Unavailable", Toast.LENGTH_LONG).show();
        }
    }


    private CurrentWeather getWeather(String jsonData) throws JSONException {
        CurrentWeather currentWeather = new CurrentWeather();

        JSONObject forecast = new JSONObject(jsonData);
        JSONArray weather = forecast.getJSONArray("weather");
        JSONObject data = weather.getJSONObject(0);

        currentWeather.setMain(data.getString("main"));

        return currentWeather;
    }

    private boolean networkIsAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean available = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            available = true;
        }
        return available;
    }


    private void errorMessage() {
        Toast.makeText(getApplicationContext(), "Error. Please try again.", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        //showLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    public void showLocation() {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            locationText.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

        } else {
            locationText.setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    public double getLatitude() {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            return location.getLatitude();
        }
        return 0;
    }

    public double getLongitude() {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            return location.getLongitude();
        }
        return 0;
    }


    private CurrentLocation getCurrentLocation() throws IOException {
        CurrentLocation currentLocation = new CurrentLocation();

        //latitude = 43.7765818;
        //longitude = -79.3277357;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0) {

            String city = addresses.get(0).getLocality();
            String province = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            currentLocation.setCity(city);
            currentLocation.setProvince(province);
            currentLocation.setCountry(country);

            return currentLocation;
        }

        currentLocation.setCity(latitude + " " + longitude);
        return currentLocation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
