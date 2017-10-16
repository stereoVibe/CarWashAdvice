package io.sokolvault13.carwashadvice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {

    final static int PERMISSION_REQUEST_CODE = 6996;
    final LocationService locationService = LocationService.newInstance();
//    final LocationService locationService = null;
    private Location mLocation;
    public final MessageThread message = new MessageThread("Обработать");

    /* Callback from requestPermission() and appropriate actions depends on
     * granted permissions */


    @Override
    public synchronized void onRequestPermissionsResult(int requestCode,
                                                        @NonNull String[] permissions,
                                                        @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.

                    final FusedLocationProviderClient mLocationProviderClient;
                    mLocationProviderClient = LocationService.getLocationProvider(this);

                    locationService.setLocationProviderClient(mLocationProviderClient);
                    locationService.setMainActivity(this);
                    LocationService.setInstance(locationService);

//                    locationService.setMessage(message);

                    if (locationService.getUpdateLocation() != null) {
                        LocationService.deleteAsyncUpdateLocation();
                    }
                    try {
                        locationService.run(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public synchronized void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_CODE);
    }

    protected synchronized void requestCoordinates(AppCompatActivity activity, boolean forceReload){

        TextView textView1 = activity.findViewById(R.id.hello_text);
        ProgressBar progressBar1 = activity.findViewById(R.id.progressBar2);

        if (locationService.getCoordinates() != null){
            if (!forceReload) {
                progressBar1.setVisibility(View.GONE);
                textView1.setVisibility(View.VISIBLE);
//                textView.setText("Без потоков: "+ "\n" + locationService.getLocation().getLatitude());
                textView1.setText(String.format("Без потоков:\n %s", locationService.getCoordinates()));
            }else {
                textView1.setVisibility(View.GONE);
                progressBar1.setVisibility(View.VISIBLE);
                requestPermission();
                new Thread(() -> {
                    synchronized (message) {
                        try {
                            message.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(()->{
                            progressBar1.setVisibility(View.GONE);
                            textView1.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);
                            Log.d("Поток 1: ", "Вышли из 1-го потока");
//                        textView.post(() -> textView.setText("После всех потоков: " + "\n" + locationService.getLocation().getLatitude()));
                            textView1.post(() -> textView1.setText(String.format("После всех потоков:\n %s", locationService.getCoordinates())));
                        });
                        LocationService.getInstance().setLocation(locationService.getLocation());

                    }
                }).start();
            }
        } else {
            textView1.setVisibility(View.GONE);
            progressBar1.setVisibility(View.VISIBLE);
            requestPermission();
            new Thread(() -> {
                synchronized (message) {
                    try {
                        message.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(()->{
                        progressBar1.setVisibility(View.GONE);
                        textView1.setVisibility(View.VISIBLE);
//                    progressBar.setVisibility(View.GONE);
                        Log.d("Поток 1: ", "Вышли из 1-го потока");
//                        textView.post(() -> textView.setText("После всех потоков: " + "\n" + locationService.getLocation().getLatitude()));
                        textView1.post(() -> textView1.setText(String.format("После всех потоков:\n %s", locationService.getCoordinates())));
                    });
                    LocationService.getInstance().setLocation(locationService.getLocation());

                }
            }).start();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        if (locationService.getCoordinates() != null) {
            return locationService;
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("COORDINATES", locationService.getCoordinates());
        outState.putDouble("Latitude", locationService.getLatitude());
        outState.putDouble("Longitude", locationService.getLongitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mLocation != null) {
            Log.d("После создания", String.format("Значение координат: %s", mLocation.getLatitude()));
        }

        setContentView(R.layout.activity_main);
        AppCompatActivity activity = this;

        if (getLastCustomNonConfigurationInstance() != null){
            LocationService restoredLocationService = (LocationService) getLastCustomNonConfigurationInstance();
            locationService.setCoordinates(restoredLocationService.getCoordinates());
        }

        final Button button = findViewById(R.id.button2);
//        button.setVisibility(View.INVISIBLE);
        final TextView textView = findViewById(R.id.hello_text);
        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

//              TODO: Create a description for the permission request
                Toast.makeText(getApplicationContext(),
                        "Нужно ваше разрешение :)", Toast.LENGTH_SHORT).show();

//              Повторный запрос на разрешение после показа объяснения
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
//                requestPermission();
                new Thread(() -> {
                    synchronized (message){
                        try {
                            requestPermission();
                            message.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        requestCoordinates(activity, false);

        button.setOnClickListener((v -> requestCoordinates(activity, true)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationService.deleteAsyncUpdateLocation();
    }
}
