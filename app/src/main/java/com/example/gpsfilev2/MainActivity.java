package com.example.gpsfilev2;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Boolean nop = false;
    Boolean cacher = true;
    Button add;
    Boolean addConf = false;
    Boolean inZone= false;
    Float diffZone = (float)2;

//////////MAIN
    public static final int MAXI = 3011;

    String conf = null;

    MyFiles myFiles;

    EditText fileName;
    EditText contenu;
    boolean confirmation = false;

    TextView v;

    Switch capture;



/////////Affiche
    int count = 0;

    Dessin dessin;

    EditText echelle;
    TextView textLoc;
    Switch aSwitchManuel;

    MyLocalisation myLocalisation;
/////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                }else{
                    capture.setVisibility(View.VISIBLE);
                    fileName.setVisibility(View.VISIBLE);
                    echelle.setVisibility(View.VISIBLE);
                    contenu.setVisibility(View.VISIBLE);
                    add.setVisibility(View.VISIBLE);

                }


            }
        });

        init();
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


        if (id == R.id.testNOP) {
            nop = true;
            return true;
        } else if (id == R.id.action_settings) {
            diffZone = (float)10;
            dessin.diffZone = diffZone;
            return true;
        } else if (id == R.id.action_t3) {
            cacher = true;
            aSwitchManuel.setChecked(true);
            dessin.manual = false;
            fileName.setText("test3");
            miseAjourDuDessin();
            return true;
        } else if (id == R.id.diffZoneP || id == R.id.diffZoneM) {
            if (id == R.id.diffZoneP && diffZone<10){
                    diffZone += (float) 1;
            } else if (id == R.id.diffZoneM && diffZone>0) {
                    diffZone -= (float) 1;
            }
            dessin.diffZone = diffZone;
            vtext("d"+diffZone.toString());
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
           item.setTitle(dessin.diffZone.toString());
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
        }
        return super.onOptionsItemSelected(item);
    }




    void miseAjourDuDessin(){
        ////
        dessin.lp.clear();
        String [] tabCorr;

        MyFiles myFiles = new MyFiles(this);
        String cont = myFiles.lireSimple(fileName.getText().toString());
        if( cont != "File not exist"){

            tabCorr = cont.split("\n");

            if( tabCorr.length >= 1) {

                for (int i = 0; i < tabCorr.length ; i++) {
                    String sss =tabCorr[i];
                    Boolean bleu = false;
                    if(tabCorr[i].contains("#")) {
                        bleu = true;
                        sss = tabCorr[i].replace("#","");
                    }
                    String[] longlat = sss.split("@");
                    if (longlat.length >= 2) {
                        float x = Float.parseFloat(longlat[0]);
                        float y = Float.parseFloat(longlat[1]);
                        if(bleu){
                            dessin.ajoutPointBleu((int) (x * 100000), (int) (y * 100000));
                        }else {
                            dessin.ajoutPoint((int) (x * 100000), (int) (y * 100000));
                        }
                    }
                }
            }



        }else {
            System.out.println("File not exist");
        }

        /////
    }

    void vtext(String s){
        v.setText(s);
    }

    void init(){


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
                    if(f != null) myFiles.ecrireFile(f,contenu.getText().toString()+"\n"+ myFiles.lireFile(f));
                    addConf = false;
                }
            }

        });







        myFiles = new MyFiles(this);

        fileName = findViewById(R.id.fileName);

        fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                file = myFiles.ouvrireFichier(fileName.getText().toString(),false);

                if(file != null) {
                    miseAjourDuDessin();
                    String cont = myFiles.lireFile(file);

                    contListView(cont);
                }else{
                    contListView("ERREUR");
                }
            }
        });


        contenu = findViewById(R.id.contenu);

        capture = findViewById(R.id.capture);

        v = findViewById(R.id.V);

        ////////////////////

        aSwitchManuel = findViewById(R.id.manuel);

        aSwitchManuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !aSwitchManuel.isChecked() && !dessin.manual){
                    dessin.manual = true;
                }else if(aSwitchManuel.isChecked() && dessin.manual){
                    dessin.manual = false;
                }
            }
        });

        dessin = findViewById(R.id.dessin);


        miseAjourDuDessin();



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
                if(aSwitchManuel.isChecked()) { majLocalisation(location); }
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
    }

    private void majLocalisation( Location l ) {
        count += 1;
        String lo = "" + String.valueOf(l.getLongitude());
        String la = "" + String.valueOf(l.getLatitude());
        textLoc.setText( lo+"@"+la+"\n="+count);

        majLaLoLocalisation(lo,la,false);
    }

    private void majLaLoLocalisation(String lo,String la, boolean bleu){
        /// ?????
        int x = (int) (Float.parseFloat(lo) * 100000);
        int y = (int) (Float.parseFloat(la) * 100000) ;

        dessin.modifRef( x , y );


        if( capture.isChecked() || bleu){

            File file;
            file = myFiles.ouvrireFichier(fileName.getText().toString(),false);

            if(file != null) {
                String cont = myFiles.lireFile(file);
                if (cont == "File not exist") {
                    cont = "";
                }
                inZone = false;
                for (Point p:dessin.lp) {
                    if (p.zone((float) x, (float) y,diffZone) && p.couleur== Color.BLACK) { inZone = true;}
                }
                if (!inZone) {
                    if(bleu){
                        myFiles.ecrireFile(file, lo + "@" + la + "#\n" + cont);
                        dessin.ajoutPointBleu(x, y);
                    }else {
                        myFiles.ecrireFile(file, lo + "@" + la + "\n" + cont);
                        dessin.ajoutPoint(x, y);
                    }
                }
                //contListView(myFiles.lireFile(file));

            }else{
                v.setText(" err ");
            }
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

        if (nbL < MAXI || confirmation) {
            contenu.setText(s);
            confirmation = false;
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        myLocalisation.preOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nop == false){
            myLocalisation.preOnPause();
        }
    }















}
