package me.selinali.drizzle;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationProvider
{
    public static Location getLocation(Context context)
    {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}
