package com.example.gpsfilev2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.util.Date;

public class MyServiceGPS extends Service {

   MyLocalisation myLocalisation = null;
   MyFiles myFiles = null;
   File file = null;

   int count = 0;

   Boolean bing_service = false;

   private static final String CHANNEL_ID = "gps_channel_id";

   @Override public void onCreate() {
      super.onCreate();
      Log.d("SERVICE", "onCreate");

      // 🔥 1. Création du canal de notification (obligatoire Android 8+)
      createNotificationChannel();
      // 🔥 2. Notification persistante
      Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
              .setContentTitle("GPS actif")
              .setContentText("Le service GPS enregistre votre position…")
              .setSmallIcon(android.R.drawable.ic_menu_mylocation)
              .setPriority(NotificationCompat.PRIORITY_LOW)
              .setOngoing(true) .build();
      // 🔥 3. Lancer le service en mode Foreground
      startForeground(1, notification);

      // Enregistrer le receiver
      IntentFilter filter = new IntentFilter();
      filter.addAction("APP_IN_BACKGROUND");
      filter.addAction("APP_IN_FRONT");
      registerReceiver(serviceReceiver, filter);


      LocationListener ln = new LocationListener() {
          @Override
          public void onLocationChanged(Location location) {
             majLocalisation(location);
          }
          @Override
          public void onStatusChanged(String s, int i, Bundle bundle) {

          }
          @Override
          public void onProviderEnabled(String s) {

          }
          @Override
          public void onProviderDisabled(String s) {

          }
      };
      myLocalisation = new MyLocalisation(this,ln);
      myFiles = new MyFiles(this);
    }

   // 🔧 Création du canal de notification
   private void createNotificationChannel() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel channel = new NotificationChannel(
                 CHANNEL_ID,
                 "GPS Tracking",
                 NotificationManager.IMPORTANCE_LOW
         );
         NotificationManager manager = getSystemService(NotificationManager.class);
         manager.createNotificationChannel(channel);
      }
   }
   private void updateNotification(String newText) {
      Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
              .setContentTitle("GPS actif")
              .setContentText(newText)
              .setSmallIcon(android.R.drawable.ic_menu_mylocation)
              .setOngoing(true)
              .build();

      NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      manager.notify(1, notification); // même ID que startForeground
   }

   private void majLocalisation( Location l ) {
      long t = new Date().getTime();
      String lo = "" + String.valueOf(l.getLongitude());
      String la = "" + String.valueOf(l.getLatitude());
      if (file != null) {
         myFiles.ecrireFile(file, "\n" + lo + "@" + la + "@time@" + t, true);
         Log.d("SERVICE", "majLocalisation MyServiceGPS  " + lo + "@" + la + "@time@" + t);
         count = count + 1;
         updateNotification("GPS actif :  count=" + count +" "+ la + ", " + lo);
      }else{
         Log.d("SERVICE", "majLocalisation MyServiceGPS no file");
      }
   }

   private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);

            if ("APP_IN_BACKGROUND".equals(action)) {
               Log.d("SERVICE", "Reçu : APP_IN_BACKGROUND");
               file = myFiles.ouvrireFichier(prefs.getString("fileName", "test3"), false);
               myLocalisation.preOnResume();
               updateNotification("APP_IN_BACKGROUND file:" + (file != null));
            }
            if ("APP_IN_FRONT".equals(action)) {
               Log.d("SERVICE", "Reçu : APP_IN_FRONT");
               myLocalisation.preOnPause();
               updateNotification("APP_IN_FRONT");
               count = 0;
            }
      }
   };

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      return START_NOT_STICKY;
   }


   @Override
   public void onTaskRemoved(Intent rootIntent) {
      stopSelf(); // arrête le service
      Log.d("SERVICE", "MyServiceGPS onTaskRemoved");
      super.onTaskRemoved(rootIntent);
   }

   @Override
   public void onDestroy() {
      stopForeground(true); // retire la notification
      unregisterReceiver(serviceReceiver);
      Log.d("SERVICE", "Service détruit !");
      super.onDestroy();
   }

   @Override
   public IBinder onBind(Intent intent) {
      // TODO: Return the communication channel to the service.
      return null;
   }
}
