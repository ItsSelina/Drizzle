package me.selinali.drizzle;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationProvider {
  public static Location getLocation(Context context) {
    LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    return manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
  }

  @Nullable
  public static String formatLocation(Location location, Context context) {
    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    try {
      List<Address> addresses = geocoder.getFromLocation(
          location.getLatitude(), location.getLongitude(), 1);

      return addresses == null ? null : String.format("%s, %s",
          addresses.get(0).getLocality(),
          addresses.get(0).getAdminArea());
    } catch (IOException exception) {
      return null;
    }
  }
}
