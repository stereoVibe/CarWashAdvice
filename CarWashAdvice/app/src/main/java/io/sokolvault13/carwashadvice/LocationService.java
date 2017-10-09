package io.sokolvault13.carwashadvice;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService {

    private double latitude;
    private double longitude;
    private FusedLocationProviderClient mLocationProviderClient;

    private LocationService(){
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static LocationService newInstance(){
        return new LocationService();
    }

    public static FusedLocationProviderClient getLocationProvider (Activity activity){
        return LocationServices.getFusedLocationProviderClient(activity);
    }

    @Override
    public String toString() {
        return "{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }


    public LocationService getLastLocation(FusedLocationProviderClient mLocationProviderClient,
                                           MainActivity mainActivity) throws SecurityException {
        mLocationProviderClient.getLastLocation()
                .addOnSuccessListener(mainActivity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            setLatitude(location.getLatitude());
                            setLongitude(location.getLongitude());
//                            textView.setText(locationService.toString());
//                            Log.i(TAG, "test");
                        }
                    }

                });

        return this;
    }
}
