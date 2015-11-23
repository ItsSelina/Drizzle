package me.selinali.drizzle.ui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
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

public class IntroActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location location;
    private GoogleApiClient googleApiClient;
    private CurrentWeather currentWeather;
    private double latitude = 0;
    private double longitude = 0;

    @Bind(R.id.location_edittext)
    EditText locationEditText;

    @Bind(R.id.weather_text)
    TextView weatherText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        buildGoogleApiClient();
    }

    @Override
    public void onConnected(Bundle bundle) {
        showLocation();
        getCoordinates();
        getForecast(latitude, longitude);
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
        getCoordinates();

        if (latitude != 0 && longitude != 0) {
            CurrentLocation currentLocation = null;

            try {
                currentLocation = getCurrentLocation(latitude, longitude);
            } catch (IOException e) {
                e.printStackTrace();
            }

            locationEditText.setText(currentLocation.getAddress());
        } else {
            locationEditText.setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    private CurrentLocation getCurrentLocation(double latitude, double longitude) throws IOException {
        CurrentLocation currentLocation = new CurrentLocation();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

        if (addresses.size() > 0) {

            String address = addresses.get(0).getAddressLine(1);

            currentLocation.setAddress(address);

            return currentLocation;
        }
        return currentLocation;
    }

    private void getForecast(double latitude, double longitude) {
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
                                    String weather = currentWeather.getMain().substring(0, 1).toUpperCase() + currentWeather.getMain().substring(1);
                                    weatherText.setText(weather);
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

    public void getCoordinates() {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }
}
