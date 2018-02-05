package com.es2.mapacrud;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import static com.es2.mapacrud.MainActivity.LATITUDE;
import static com.es2.mapacrud.MainActivity.LONGITUDE;
import static com.es2.mapacrud.MainActivity.IMGNUM;
import static com.es2.mapacrud.MainActivity.X;
import static com.es2.mapacrud.MainActivity.Y;
import static com.es2.mapacrud.MainActivity.Z;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private float latitude, longitude, x, y, z;
    private int imgNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (null != intent){
            latitude = intent.getFloatExtra(LATITUDE, latitude);
            longitude = intent.getFloatExtra(LONGITUDE, longitude);
            imgNum = intent.getIntExtra(IMGNUM, imgNum);
            x = intent.getFloatExtra(X, x);
            y = intent.getFloatExtra(Y, y);
            z = intent.getFloatExtra(Z, z);
            CharSequence latLong = "Latitude: " + latitude + " Longitude: " + longitude + " ImgNum: " + imgNum
                                    + "Incl.(X, Y, Z): " + x + "; " + y
                                    + "; " + z;
            Log.e("LOL: ", latLong.toString());
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double nlat;
        double nlong;

        String snippet = String.format(Locale.getDefault(),
                "Lat: %1$.2f; Long: %2$.2f; Incl(X, Y, Z): %3$.2f; %4$.2f; %5$.2f",
                latitude,
                longitude,
                x, y, z);
        nlat= Math.round(latitude*10000)/10000.0d;
        nlong= Math.round(longitude*10000)/10000.0d;
        LatLng pos = new LatLng(nlong, nlat);
        float zoom = 16.0f;
        mMap.addMarker(new MarkerOptions().position(pos).title("Imagem " + (imgNum+1)).snippet(snippet));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
    }
}
