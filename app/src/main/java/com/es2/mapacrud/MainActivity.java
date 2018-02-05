package com.es2.mapacrud;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.es2.mapacrud.database.models.Photo;
import com.es2.mapacrud.database.controllers.PhotoController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private SensorManager sensorManager;
    private SensorEventListener mEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                getAccelerometer(sensorEvent);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        private void getAccelerometer(SensorEvent event) {
            float[] values = event.values;
            mValuesOrientation[0] = values[0];
            mValuesOrientation[1] = values[1];
            mValuesOrientation[2] = values[2];
        }
    };

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String IMGNUM = "imgnum";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    private PhotoController photoController;

    private LocationManager g_locationManager;
    private Location g_loc;
    private boolean g_isGPS = false;
    private boolean g_isNetwork = false;
    private boolean g_canGetLocation = true;


    private Button takePhotoButton;
    private ImageView takenImageView;
    private ListView listView;

    private String mCurrentPhotoPath;
    private double latitude, longitude;


    private String bestProvider;
    private Criteria criteria;
    private CharSequence test;

    final float[] mValuesMagnet      = new float[3];
    final float[] mValuesAccel       = new float[3];
    final float[] mValuesOrientation = new float[3];
    final float[] mRotationMatrix    = new float[9];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Conecta ao BD e carrega as fotos
        photoController = new PhotoController(this);
        photoController.open();
        this.fillImagesList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent newIntent = new Intent(view.getContext(), MapsActivity.class);
                Object o = listView.getItemAtPosition(i);
                Photo p = (Photo) o;
                CharSequence latLong = "Latitude: " + p.getLat() + " Longitude" + p.getLng();
                newIntent.putExtra(LATITUDE, p.getLat());
                newIntent.putExtra(LONGITUDE, p.getLng());
                newIntent.putExtra(IMGNUM, i);
                newIntent.putExtra(X, p.getX());
                newIntent.putExtra(Y, p.getY());
                newIntent.putExtra(Z, p.getZ());
                startActivityForResult(newIntent, 0);
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        setListners(sensorManager, mEventListener);

        takePhotoButton = findViewById(R.id.photoButton);

        g_locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        g_isGPS = g_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        g_isNetwork = g_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        if (!g_isGPS && !g_isNetwork) {
            Log.d("LOL: ", "Connection off");
            showSettingsAlert();
            getLastLocation();
        }



        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();
            }
        });
    }


    private void fillImagesList () {
        List<Photo> Lphotos = photoController.getAllPhotos();
        Log.i("lol1:", Lphotos.toString());
        CustomListAdapter adapter = new CustomListAdapter(this, Lphotos.toArray(new Photo[Lphotos.size()]).length == 0 ? new Photo[0] : Lphotos.toArray(new Photo[Lphotos.size()]));

        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }






    /* REQUEST PERMISSIONS */

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
            }
        }
        return;
    }

    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 1);
        }
        else {
            dispatchTakePictureIntent();
        }
    }

    /* ------------------- */

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider", photoFile);
                getLocation();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Photo new_photo = new Photo();
            new_photo.setPath(mCurrentPhotoPath);
            new_photo.setLng((float)latitude);
            new_photo.setLat((float)longitude);
            new_photo.setX(mValuesOrientation[0]);
            new_photo.setY(mValuesOrientation[1]);
            new_photo.setZ(mValuesOrientation[2]);
            photoController.addPhoto(new_photo);
            this.fillImagesList();
        }
    }



    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }


    @Override
    public void onProviderDisabled(String s) {
        if (g_locationManager != null) {
            g_locationManager.removeUpdates(this);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("LOL:", "Location Changed! Lat: " + location.getLatitude() + "Long: " +location.getLongitude() + "Gyro: " + location.getBearing());
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}



    /* FUNÇÕES UTILITÁRIAS*/

    private void getLocation() {
        try {
            if (g_canGetLocation) {
                Log.d("LOL: ", "Can get location");
                if (g_isGPS) {
                    // from GPS
                    Log.d("LOL: ", "GPS on1" + g_loc);
                    g_locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    //A porcaria da localização e inclinação tá aqui nesse carai, até que enfim!

                    criteria = new Criteria();
                    bestProvider = String.valueOf(g_locationManager.getBestProvider(criteria, true)).toString();
                    g_loc = g_locationManager.getLastKnownLocation(bestProvider);
                    Log.d("LOL: ", "Lo" + g_loc);

                    if (g_loc!=null){
                        Log.e("LOL:", "GPS is on!");
                        latitude = g_loc.getLatitude();
                        longitude = g_loc.getLongitude();
                        //bearing = loc.getBearing();

                        sensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
                        sensorManager.getOrientation(mRotationMatrix, mValuesOrientation);

                        test = "results: " + mValuesOrientation[0] +" "+mValuesOrientation[1]+ " "+ mValuesOrientation[2];

                    } else{
                        g_locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                    }
                    Log.d("LOL: ", "lat: " + latitude + " long: " + longitude + test);

                } else if (g_isNetwork) {
                    // from Network Provider
                    Log.d("LOL: ", "NETWORK_PROVIDER on");
                    g_locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                } else {
                    g_loc.setLatitude(0);
                    g_loc.setLongitude(0);
                    Log.d("LOL: ", "Teste deb");
                }
            } else {
                Log.d("LOL: ", "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void setListners(SensorManager sensorManager, SensorEventListener mEventListener)
    {
        sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        /*sensorManager.registerListener(mEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);*/
    }



    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = g_locationManager.getBestProvider(criteria, false);
            Location location = g_locationManager.getLastKnownLocation("gps");
            Log.d("LOL:", provider);
            Log.d("LOL:", location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}
