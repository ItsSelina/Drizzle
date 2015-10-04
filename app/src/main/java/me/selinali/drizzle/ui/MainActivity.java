package me.selinali.drizzle.ui;

import android.app.WallpaperManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.selinali.drizzle.R;
import me.selinali.drizzle.weather.CurrentWeather;


public class MainActivity extends AppCompatActivity {

    private CurrentWeather currentWeather;

    @Bind(R.id.current_weather_text)
    TextView currentWeatherText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getForecast();

        Button buttonSetWallpaper = (Button) findViewById(R.id.set_weather_button);
        buttonSetWallpaper.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());

                EditText setWeather = (EditText) findViewById(R.id.weather_text);
                String weatherName = setWeather.getText().toString();


                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(setWeather.getWindowToken(), 0);

                try {
                    myWallpaperManager.setResource(getResources().getIdentifier(weatherName, "drawable", getPackageName())); // This returns the same integer value as R.drawable.weatherName
                    Snackbar.make(arg0, "Wallpaper set successfully.", Snackbar.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getForecast() {
        String forecastUrl = "http://api.openweathermap.org/data/2.5/weather?q=osaka,jp";

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
                            //final String main = getMain(jsonData);
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
