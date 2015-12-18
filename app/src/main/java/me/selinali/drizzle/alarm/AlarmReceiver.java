package me.selinali.drizzle.alarm;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "me.selinali.drizzle.PREF";
    private static final String CURRENT_ICON = "CURRENT_ICON";

    SharedPreferences sharedPreferences;
    String currentIcon;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Tada!", Toast.LENGTH_SHORT).show();

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentIcon = sharedPreferences.getString(CURRENT_ICON, "empty");

        int currentId = context.getResources().getIdentifier(currentIcon, "drawable", context.getPackageName());

        String newWallpaperId = getRandomDrawableName(String.valueOf(currentId));
        saveToSharedPreferences(newWallpaperId);

        try {
            wallpaperManager.setResource(context.getResources().getIdentifier("c" + newWallpaperId, "drawable", context.getPackageName()));
        } catch (IOException e) {
            Toast.makeText(context, "Could not change wallpaper", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToSharedPreferences(String newWallpaperId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_ICON, newWallpaperId).apply();
    }

    public String getRandomDrawableName(String lastWallpaperId) {
        String[] wallpaper = new String[] {
                "01d",
                "01n",
                "02d",
                "02n",
                "03d",
                "03n",
                "04d",
                "04n",
                "09d",
                "09n",
                "10d",
                "10n",
                "11d",
                "11n",
                "13d",
                "13n",
                "50d",
                "50n",
        };

        int min = 0;
        int max = wallpaper.length - 1;
        int wallpaperNumber;
        String newWallpaperId = lastWallpaperId;

        while(newWallpaperId.equals(lastWallpaperId)) {
            Random r = new Random();
            wallpaperNumber = r.nextInt(max - min + 1) + min;
            newWallpaperId = wallpaper[wallpaperNumber];
        }

        return newWallpaperId;
    }
}
