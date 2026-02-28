package com.example.gpsfilev2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Boolean nop = false;
    Boolean cacher = true;
    Button add;
    Boolean addConf = false;
    Boolean inZone= false;
    Float diffZone = (float)5;
    Boolean continu = false;

//////////MAIN
    public static final int MAXI = 3011;

    String conf = null;

    MyFiles myFiles;

    MenuItem menuItemX;

    Button bVu;

    EditText fileName;
    EditText contenu;
    boolean confirmation = false;

    TextView v;

    Switch capture;
    //Switch ptVU;


/////////Affiche
    int count = 0;

    Dessin dessin;

    EditText echelle;
    EditText editTextici;
    TextView textLoc;
    Switch aSwitchManuel;

    MyLocalisation myLocalisation;

    Point lastpoint;

/////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        Log.d("APP", "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cacher = !cacher;
 //               Snackbar.make(view, "Cacher ="+cacher, Snackbar.LENGTH_LONG)
 //                       .setAction("Action", null).show();
                if(cacher){
                    capture.setVisibility(View.INVISIBLE);
                    fileName.setVisibility(View.INVISIBLE);
                    echelle.setVisibility(View.INVISIBLE);
                    contenu.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.INVISIBLE);
                    editTextici.setVisibility(View.INVISIBLE);
                    bVu.setVisibility(View.INVISIBLE);

                }else{
                    capture.setVisibility(View.VISIBLE);
                    fileName.setVisibility(View.VISIBLE);
                    echelle.setVisibility(View.VISIBLE);
                    contenu.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);
                    editTextici.setVisibility(View.VISIBLE);
                    bVu.setVisibility(View.VISIBLE);
                }


            }
        });

        init();

        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);

        //fileName fileName.getText().toString(); fileName.setText("test3");   miseAjourDuDessin();
        fileName.setText(prefs.getString("fileName", "test3"));
        if (!fileName.getText().toString().equals("test3") ) miseAjourDuDessin();
        // echelle // du dessin  dessin.modifEchelle(Float.parseFloat(echelle.getText().toString()));
        echelle.setText(prefs.getString("echelle", "200")); dessin.modifEchelle(Float.parseFloat(echelle.getText().toString()));
        // aSwitchManuel.isChecked() dessin.manual = true;
        boolean manual = prefs.getBoolean("manual",false);
        aSwitchManuel.setChecked(manual);dessin.manual = manual;
        // capture.isChecked()
        capture.setChecked(false);

        // diffZone = (float)10;dessin.diffZone = diffZone; vtext("X"+diffZone.toString());
        diffZone = prefs.getFloat("diffZone",diffZone);dessin.diffZone = diffZone; vtext("X"+diffZone.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        //
        menuItemX = findViewById(R.id.menuTest);

        if (id == R.id.testNOP) {
            //nop = true;
            diffZone = (float)2;
            dessin.diffZone = diffZone;
            vtext("X"+diffZone.toString());
            //item.setTitle("X"+dessin.diffZone.toString());
            //menuItemX.setTitle("X"+dessin.diffZone.toString());
            return true;
        }else if(id == R.id.X5) {
            diffZone = (float)5;
            dessin.diffZone = diffZone;
            vtext("X"+diffZone.toString());
        }else if (id == R.id.action_settings) {
            diffZone = (float)10;
            dessin.diffZone = diffZone;
            vtext("X"+diffZone.toString());
            return true;
        } else if (id == R.id.action_t3) {
            cacher = true;
            aSwitchManuel.setChecked(true);
            dessin.manual = false;
            fileName.setText("test3");
            miseAjourDuDessin();
            return true;
        } else if (id == R.id.diffZoneP || id == R.id.diffZoneM) {
            if (id == R.id.diffZoneP ){
                    diffZone += (float) 10;
            } else if (id == R.id.diffZoneM && diffZone>2) {
                    diffZone -= (float) 5;
            }
            dessin.diffZone = diffZone;
            vtext("X"+diffZone.toString());
            return true;
        }else if(id == R.id.captureMenu){
            if(!capture.isChecked()) {
                aSwitchManuel.setChecked(true);
                capture.setChecked(true);
                dessin.manual = false;
                item.setTitle("CaptOUI");
            }else{
                capture.setChecked(false);
                dessin.manual = true;
                item.setTitle("CaptNON");
            }
            return true;
        }else if (id == R.id.menuTest) {
            diffZone = Float.parseFloat(contenu.getText().toString());
            dessin.diffZone = diffZone;
            vtext("X"+diffZone.toString());
            item.setTitle("X"+diffZone.toString());
        }else if( id == R.id.export0){
            String name = fileName.getText().toString();
            String cont =myFiles.lireSimple(name);

            File file0 = myFiles.ouvrireFichier(name,true);
            myFiles.ecrireFile(file0,cont);
          //  myFiles.inOut = false;

        }else if( id == R.id.import0){

                String name = fileName.getText().toString();
                myFiles.inOut = true;
                String cont =myFiles.lireSimple(name);
                myFiles.inOut = false;
                File file0 = myFiles.ouvrireFichier(name,false);
                myFiles.ecrireFile(file0,cont);


        }else if( id == R.id.bleu){
            Point p = dessin.ref;
            majLaLoLocalisation((p.x/100000)+"",(p.y/100000)+"",true);
        }else if(id == R.id.continu){
            continu = !continu;
            item.setTitle("continu:"+continu);
        }
        return super.onOptionsItemSelected(item);
    }




    void miseAjourDuDessin(){
        ////
        dessin.lp.clear();
        String [] tabCorr;

        String cont = myFiles.lireSimple(fileName.getText().toString());
        if( !cont.equals("File not exist") ){

            tabCorr = cont.split("\n");

            if( tabCorr.length >= 1) {
                long t = new Date().getTime();
                for (int i = 0; i < tabCorr.length ; i++) {
                    String sss =tabCorr[i];
                    long time=0;
                    String desc ="";
                    Boolean bleu = false;


                    // 10/9/20
                    String[] tab;
                    if(sss.contains("@time@")){
                        tab = sss.split("@time@");
                        sss = tab[0];
                        if(tab.length==2){
                            // timer tab[1];
                            time = Long.parseLong(tab[1]);
                        }
                        //System.out.println("0:"+sss);
                    }

                    tab = null;

                    if(sss.contains("#")) {
                        //System.out.println("1:"+sss);
                        bleu = true;
                        tab = sss.split("#");
                        sss = tab[0];
                        // 10/9/20
                        if(tab.length==2) {
                            desc = tab[1];
                        }
                        //sss = sss.replace("#","");
                        //System.out.println("2:"+sss);
                    }
                    String[] longlat = sss.split("@");
                    if (longlat.length >= 2) {
                        float x = Float.parseFloat(longlat[0]);
                        float y = Float.parseFloat(longlat[1]);
                        // 10/9/20
                        if(bleu){
                            dessin.ajoutPointBleu((int) (x * 100000), (int) (y * 100000),time,desc,false);
                        }else {
                            if ( (t - time) < (24L * 60L * 60L * 1000L) ){
                                dessin.ajoutPointCyan((int) (x * 100000), (int) (y * 100000),time);
                            }else {
                                dessin.ajoutPoint((int) (x * 100000), (int) (y * 100000), time);
                            }
                        }
                    }
                }
                Log.d("APP", "miseAjourDuDessin");
                dessin.refresh();
            }



        }else {
            System.out.println("File not exist0");
        }

        /////
    }

    void vtext(String s){
        v.setText(s);
    }

    void init(){
        lastpoint=null;
        v = findViewById(R.id.V);

        menuItemX = findViewById(R.id.menuTest);

        bVu = findViewById(R.id.pointVu);

        bVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = contenu.getText().toString();
                if(s.contains("@")){
                    String tab[] = s.split("@");
                    if(tab.length==2) {

                        int x = (int) (Float.parseFloat(tab[0]) * 100000);
                        int y = (int) (Float.parseFloat(tab[1]) * 100000) ;

                        dessin.modifRef(x, y);

                       if (x !=0 && y !=0) {
                           dessin.setPointVu(new Point(x, y, Color.RED, 0));
                       }
                    }
                }
            }
        });


        add = findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(contenu.getText().toString().contains("LIRE")){

                    contListView("Lecture autoriser");
                    confirmation = true;
                    vtext("L");
                }else {
                    confirmation = false;
                    vtext("");
                }

                if(contenu.getText().toString().contains("SUP")){
                    File f= myFiles.ouvrireFichier(fileName.getText().toString(),false);
                    if(f != null) myFiles.ecrireFile(f,"");
                }


                if(!addConf){
                    if(contenu.getText().toString().contains("ADD")) {
                        add.setText("++");
                        addConf = true;
                    }else{
                        add.setText("ADD");
                        contenu.setText("");
                    }
                }else{
                    add.setText(";-)");
                    File f= myFiles.ouvrireFichier(fileName.getText().toString(),false);
                    if(f != null) myFiles.ecrireFile(f,"\n"+contenu.getText().toString(),true);
                    addConf = false;
                }
            }

        });







        myFiles = new MyFiles(this);

        fileName = findViewById(R.id.fileName);

        fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("---fileName-onClick--");
                if(myFiles.existe(fileName.getText().toString())) {
                    miseAjourDuDessin();
                    if(confirmation) {
                        File file = myFiles.ouvrireFichier(fileName.getText().toString(), false);
                        String cont = myFiles.lireFile(file);
                        contListView(cont);
                        System.out.println("---fileName-onClick-lireFile-");
                    }
                }else{
                    contListView("ERREUR");
                }
            }
        });


        contenu = findViewById(R.id.contenu);

        capture = findViewById(R.id.capture);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capture.isChecked()) {
                    Intent intent = new Intent(MainActivity.this, MyServiceGPS.class);
                    ContextCompat.startForegroundService(MainActivity.this, intent);

                    // Android 13 : permission pour notifications
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS)
                                    != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                                1002);
                    }

                } else {
                    stopService(new Intent(MainActivity.this, MyServiceGPS.class));
                }
            }
        });

        editTextici = findViewById(R.id.editTextici);
        //ptVU = findViewById(R.id.ptVu);



        ////////////////////

        aSwitchManuel = findViewById(R.id.manuel);

        aSwitchManuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !aSwitchManuel.isChecked() && !dessin.manual){
                    dessin.manual = true;
                }else if(aSwitchManuel.isChecked() && dessin.manual){
                    dessin.manual = false;
                    dessin.returnRef();
                }
            }
        });

        dessin = findViewById(R.id.dessin);


        miseAjourDuDessin();

        if(dessin.lp.size()>0)lastpoint= dessin.lp.get(dessin.lp.size()-1);


        echelle = findViewById(R.id.echelle);

        echelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dessin.modifEchelle(Float.parseFloat(echelle.getText().toString()));
            }
        });



        textLoc = findViewById(R.id.textLoc);

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
        myLocalisation.preOnResume();
    }

    private void demanderPermissions() {

        // Permissions classiques
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    1000);
        }

        // Android 10+ : localisation en arrière-plan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // Afficher un dialogue explicatif avant la demande
            new AlertDialog.Builder(this)
                    .setTitle("Autorisation requise")
                    .setMessage("Pour que l'application fonctionne correctement, elle doit accéder à votre position même lorsque l'application est fermée ou en arrière‑plan.")
                    .setPositiveButton("Autoriser", (dialog, which) -> {
                        ActivityCompat.requestPermissions(
                                this,
                                new String[]{ Manifest.permission.ACCESS_BACKGROUND_LOCATION },
                                1001
                        );
                    })
                    .setNegativeButton("Annuler", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();
        }

    }



    private void majLocalisation( Location l ) {
        count += 1;
        String lo = "" + String.valueOf(l.getLongitude());
        String la = "" + String.valueOf(l.getLatitude());
        textLoc.setText( la+"@"+lo+"\n="+count);
        editTextici.setText(la+"@"+lo);
        majLaLoLocalisation(lo,la,false);
    }

    private void majLaLoLocalisation(String lo,String la, boolean my_ble){
        /// ?????
        int x = (int) (Float.parseFloat(lo) * 100000);
        int y = (int) (Float.parseFloat(la) * 100000) ;

        if(aSwitchManuel.isChecked()) {
            dessin.modifRef(x, y);
        }else{
            dessin.modifRefTrace(x,y);
        }


        if( capture.isChecked() || my_ble){

                inZone = false;
                if( lastpoint!=null && (lastpoint.zone((float) x, (float) y,10)|| !continu)){
                    System.out.println("---inzone---"+x+" "+y+ "  "+lastpoint.x+" "+lastpoint.y);

                    int i=0;
                    while(!inZone && i<dessin.lp.size()){
                        Point p = dessin.lp.get(i);
                        if (p.zone((float) x, (float) y,diffZone) && p.epaisseur>=dessin.epaisseur) {
                            inZone = true;
                        }
                        i++;
                    }
                }else{
                    if(lastpoint==null){
                        System.out.println("---lastpoint==null---");
                    }else{
                        System.out.println("---outzone---"+x+" "+y+ "  "+lastpoint.x+" "+lastpoint.y);
                    }
                }


                if (!inZone) {
                    File file = myFiles.ouvrireFichier(fileName.getText().toString(),false);
                    if(file != null) {
                    // 10/9/20
                    long t = new Date().getTime();
                    if(my_ble){
                        String desc = contenu.getText().toString();
                        myFiles.ecrireFile(file, "\n" + lo + "@" + la + "#"+desc+"@time@"+t,true); // file IN
                        dessin.ajoutPointBleu(x, y,t,desc);
                    }else {
                        myFiles.ecrireFile(file, "\n" + lo + "@" + la + "@time@"+t ,true);
                        dessin.ajoutPointCyan(x, y,t);
                        lastpoint=new Point(x,y);
                        System.out.println("---new-last-point---"+x+" "+y);
                    }
                    }else{
                        v.setText(" err ");
                    }
                }
                //contListView(myFiles.lireFile(file));


        }else{
            //contListView("Pas de capture");
        }


    }


    int nbligne(String s ){
        String [] tabCorr = s.split("\n");
        return tabCorr.length;
    }

    void contListView(String s) {

        if (cacher) return;

        int nbL = nbligne(s);
        vtext("z" + nbL);
        contenu.setText("nbL > MAXI !"+confirmation);

        if (nbL < MAXI ) {
            contenu.setText(s);
            confirmation = false;
        }
    }


    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        //fileName
        editor.putString("fileName",fileName.getText().toString());
        // echelle // du dessin  echelle.getText().toString()
        editor.putString("echelle",echelle.getText().toString());
        //  dessin.manual = true;
        editor.putBoolean("manual",aSwitchManuel.isChecked());
        // capture.isChecked()
        editor.putBoolean("capture",capture.isChecked());
        // diffZone
        editor.putFloat("diffZone",diffZone);
        editor.apply(); // async
        if (capture.isChecked() ) {
            Intent intent = new Intent("APP_IN_BACKGROUND");
            sendBroadcast(intent);
        }
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        if (capture.isChecked() ) {
            Intent intent = new Intent("APP_IN_FRONT");
            sendBroadcast(intent);
            miseAjourDuDessin();
        }
        myLocalisation.preOnResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("APP", "APP onResume !");
        demanderPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLocalisation.preOnPause();
    }

    protected void onDestroy() {
        stopService(new Intent(this, MyServiceGPS.class));
        Log.d("APP", "APP détruit !");
        super.onDestroy();
    }

}
