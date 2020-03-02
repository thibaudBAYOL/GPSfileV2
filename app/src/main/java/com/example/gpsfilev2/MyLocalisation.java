package com.example.gpsfilev2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

public class MyLocalisation {

    Context cont0;

    LocationManager lm;
    LocationListener listener;

    long minTime = 2000;


    MyLocalisation(Context cont, LocationListener locationListener){
     cont0 = cont;

     if (ContextCompat.checkSelfPermission(cont, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         // Permission is not granted
     }
     if (ContextCompat.checkSelfPermission(cont, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
         // Permission is not granted
     }


     lm = (LocationManager) cont.getSystemService(Context.LOCATION_SERVICE);

     listener = locationListener;

 }

    void changeMinTime(long s ){
        minTime = s;
    }





    void preOnResume(){


        Boolean okACCESS_FINE_LOCATION = Boolean.TRUE;
        Boolean okACCESS_COARSE_LOCATION = Boolean.TRUE;

        if (ContextCompat.checkSelfPermission(cont0, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            okACCESS_FINE_LOCATION = false;
        }

        if (ContextCompat.checkSelfPermission(cont0, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            okACCESS_COARSE_LOCATION = false;
        }

        if (okACCESS_FINE_LOCATION && okACCESS_COARSE_LOCATION) {
            if (lm != null) lm.requestLocationUpdates("gps", 2000, 1, listener);

        } else {
            System.out.print("////////////////err d'otorisation GPS\n");
/*
            StringBuilder s = new StringBuilder();

            if (!okACCESS_COARSE_LOCATION) s.append(" ACCESS_COARSE_LOCATION /\n");
            if (!okACCESS_FINE_LOCATION) s.append(" ACCESS_FINE_LOCATION ");

*/

        }
    }


    void preOnPause() {

        System.out.print("////////////////PAUSE////////////////////////////////////\n");
        lm.removeUpdates(listener);

    }




}
