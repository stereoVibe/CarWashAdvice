package io.sokolvault13.carwashadvice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {

    final static int PERMISSION_REQUEST_CODE = 6996;
    final LocationService locationService = LocationService.newInstance();
    public final MessageThread message = new MessageThread("Обработать");

    /* Callback from requestPermission() */
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
                    locationService.setMessage(message);
                    locationService.run();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button2);
        final TextView textView = findViewById(R.id.hello_text);

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

        button.setOnClickListener((view) ->
                new Thread(() -> {
                    synchronized (message){
                        try {
                            requestPermission();
                            message.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        textView.post(()-> textView.setText("Из Runnable 2: "+ "\n" + locationService.toString()));
                    }
                }).start()
        );
    }
}
