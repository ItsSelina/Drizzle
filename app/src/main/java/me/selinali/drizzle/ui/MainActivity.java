package me.selinali.drizzle.ui;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.selinali.drizzle.LocationProvider;
import me.selinali.drizzle.R;
import me.selinali.drizzle.api.WeatherApi;
import me.selinali.drizzle.api.WeatherResponse;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Bind(R.id.temperature_textview) TextView mTemperatureTextView;
  @Bind(R.id.condition_textview) TextView mConditionTextView;
  @Bind(R.id.location_textview) TextView mLocationTextView;

  private Subscription mWeatherSubscription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    Location location = LocationProvider.getLocation(this);
    mLocationTextView.setText(LocationProvider.formatLocation(location, this));
    mWeatherSubscription = WeatherApi.instance().getWeather(location)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::bind, this::handleError);
  }

  private void bind(WeatherResponse weatherResponse) {
    mTemperatureTextView.setText(String.format("%.2f °", weatherResponse.getTemperature()));
    mConditionTextView.setText(weatherResponse.getBlurb());
  }

  private void handleError(Throwable throwable) {
    Log.d(TAG, "Unable to fetch weather for current location", throwable);
    Toast.makeText(this, "Fuck!", Toast.LENGTH_LONG).show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mWeatherSubscription != null && !mWeatherSubscription.isUnsubscribed()) {
      mWeatherSubscription.unsubscribe();
    }
  }
}
