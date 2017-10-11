package io.sokolvault13.carwashadvice;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService implements Runnable {

    private double latitude;
    private double longitude;
    private Location mLocation;
    private FusedLocationProviderClient mLocationProviderClient;
    private MainActivity mainActivity;
    private MessageThread message;


    public void setLocationProviderClient(FusedLocationProviderClient locationProviderClient) {
        mLocationProviderClient = locationProviderClient;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private LocationService() {
        latitude = 1d;
        longitude = 2d;
    }

    public MessageThread getMessage() {
        return message;
    }

    public void setMessage(MessageThread message) {
        this.message = message;
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

    public String getStringLatitude() {
        return "" + latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static LocationService newInstance() {
        return new LocationService();
    }

    public static FusedLocationProviderClient getLocationProvider(MainActivity activity) {
        return LocationServices.getFusedLocationProviderClient(activity);
    }

    @Override
    public String toString() {
        return "{" +
                "latitude=" + this.latitude +
                ", longitude=" + this.longitude +
                '}';
    }

    public synchronized LocationService getLastLocation(final FusedLocationProviderClient locationProviderClient,
                                                        final MainActivity mainActivity, final MessageThread message) throws SecurityException {

        locationProviderClient.getLastLocation()
                .addOnSuccessListener(mainActivity, location -> {
                    if (location != null) {
                        setLatitude(location.getLatitude());
                        setLongitude(location.getLongitude());

                        synchronized (message) {
                            message.notify();
                        }
                    }
                });
        return LocationService.this;
    }

    @Override
    public void run() {
        LocationService locationService = LocationService.this;
        locationService.getLastLocation(mLocationProviderClient, mainActivity, message);
    }
}
