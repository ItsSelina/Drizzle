package me.selinali.drizzle;

import android.app.WallpaperManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSetWallpaper = (Button) findViewById(R.id.set_weather_button);
        buttonSetWallpaper.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getApplicationContext());

                EditText setWeather = (EditText) findViewById(R.id.weather_text);
                String weatherName = setWeather.getText().toString();

                //Hides the virtual keyboard
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(setWeather.getWindowToken(), 0);

                try {
                    myWallpaperManager.setResource(getResources().getIdentifier(weatherName, "drawable", getPackageName())); // This returns the same integer value as R.drawable.weatherName
                    Snackbar.make(arg0, "Wallpaper set successfully.", Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Wallpaper set successfully.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
