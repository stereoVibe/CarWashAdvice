package io.sokolvault13.carwashadvice;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationService{

    private double latitude;
    private double longitude;
    private Location mLocation;
    private FusedLocationProviderClient mLocationProviderClient;
    private MainActivity mainActivity;
    private MessageThread message;
    private static LocationService mSelfInstance;
    private UpdateLocation updateLocation;
    private String mCoordinates;

    public  String getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(String coordinates) {
        this.mCoordinates = coordinates;
    }

    public void setLocationProviderClient(FusedLocationProviderClient locationProviderClient) {
        mLocationProviderClient = locationProviderClient;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private LocationService() {
    }

    public MessageThread getMessage() {
        return message;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public UpdateLocation getUpdateLocation() {
        return updateLocation;
    }

    public void setMessage(MessageThread message) {
        this.message = message;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static LocationService newInstance() {
        return new LocationService();
    }
    public static void setInstance(final LocationService locationService){
        mSelfInstance = locationService;
    }
    public static LocationService getInstance() {
        return mSelfInstance;
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


    public synchronized Location getLastLocation(final FusedLocationProviderClient locationProviderClient,
                                                        final MainActivity mainActivity, final MessageThread message) throws SecurityException {

        locationProviderClient.getLastLocation()
                .addOnSuccessListener(mainActivity, this::setLocation);
        synchronized (message) {
            message.notify();
        }
        setLatitude(mLocation.getLatitude());
        setLongitude(mLocation.getLongitude());
        mCoordinates = String.format("%s,%s", mLocation.getLatitude(), getLongitude());
        return mLocation;
    }

    public synchronized void run(final MessageThread message) throws InterruptedException {
        LocationService locationService = LocationService.this;
        this.message = message;
        if (mLocation != null){
            mLocation = locationService.getLastLocation(mLocationProviderClient, mainActivity, message);
        } else {
            new Thread(() -> mainActivity.runOnUiThread(() -> {
                updateLocation = new UpdateLocation();
                updateLocation.execute(mainActivity);
            })).start();
        }
    }

    private static class UpdateLocation extends AsyncTask<MainActivity, Void, MessageThread> {

        LocationService locationService = mSelfInstance;
        final MessageThread messageThreadFromActivity = locationService.getMessage();

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(MessageThread messageThread) {
//            super.onPostExecute(messageThread);
            messageThread = messageThreadFromActivity;
            Log.d("Поток 2: ", "Вышли из 2-го потока");
        }

        @Override
        protected MessageThread doInBackground(MainActivity... mainActivities) {

            if (mSelfInstance != null) {
                synchronized (messageThreadFromActivity) {
                    Log.d("Поток 2: ", "Привет из 2-го потока");
                    locationService.getSingleLocationUpdateFromNetworkService(mainActivities[0], messageThreadFromActivity);
                    try {
                        messageThreadFromActivity.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                messageThreadFromActivity.notifyAll();
            }
            return null;
        }
    }

    public static void deleteAsyncUpdateLocation(){

        if (!mSelfInstance.getUpdateLocation().isCancelled()){
            mSelfInstance.getUpdateLocation().cancel(true);
        }
    }

    private synchronized void getSingleLocationUpdateFromNetworkService(MainActivity mainActivity, MessageThread message) {

        LocationManager locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        Log.d("Поток 2: ", "Установили менеджер из 2-го потока");
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                synchronized (message){
                    setLocation(location);
                    mCoordinates = String.format("%s,%s", mLocation.getLatitude(), mLocation.getLongitude());
                    Log.d("Поток 2: ", "данные из экземпляра: " + mLocation.getLatitude());
                    synchronized (message) {
                        message.notify();
                    }
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ContextCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && locationManager != null) {
//            TODO; change LocationManager to NETWORK_PROVIDER before release
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, Looper.getMainLooper());
            Log.d("Поток 2: ", "Вышли из метода 2-го потока");
        }
//        return mLocation;
    }



}
