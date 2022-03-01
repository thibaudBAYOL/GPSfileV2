package com.example.gpsfilev2;

import android.content.ClipData;
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
            //item.setTitle("X"+dessin.diffZone.toString());
            //menuItemX.setTitle("X"+dessin.diffZone.toString());
            return true;
        }else if(id == R.id.X5) {
            diffZone = (float)5;
            dessin.diffZone = diffZone;
        }else if (id == R.id.action_settings) {
            diffZone = (float)10;
            dessin.diffZone = diffZone;
            //item.setTitle("X"+dessin.diffZone.toString());
            //menuItemX.setTitle("X"+dessin.diffZone.toString());
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
            vtext("X"+diffZone.toString());
            //menuItemX.setTitle("X"+dessin.diffZone.toString());
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
           item.setTitle("X"+dessin.diffZone.toString());
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

        MyFiles myFiles = new MyFiles(this);
        String cont = myFiles.lireSimple(fileName.getText().toString());
        if( !cont.equals("File not exist") ){

            tabCorr = cont.split("\n");

            if( tabCorr.length >= 1) {

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
                            dessin.ajoutPointBleu((int) (x * 100000), (int) (y * 100000),time,desc);
                        }else {
                            dessin.ajoutPoint((int) (x * 100000), (int) (y * 100000),time);
                        }
                    }
                }
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
                }
            }
        });

        dessin = findViewById(R.id.dessin);


        miseAjourDuDessin();

        if(dessin.lp.size()>0)lastpoint= dessin.lp.get(0);


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
    }

    private void majLocalisation( Location l ) {
        count += 1;
        String lo = "" + String.valueOf(l.getLongitude());
        String la = "" + String.valueOf(l.getLatitude());
        textLoc.setText( lo+"@"+la+"\n="+count);
        editTextici.setText(lo+"@"+la);
        majLaLoLocalisation(lo,la,false);
    }

    private void majLaLoLocalisation(String lo,String la, boolean bleu){
        /// ?????
        int x = (int) (Float.parseFloat(lo) * 100000);
        int y = (int) (Float.parseFloat(la) * 100000) ;

        if(aSwitchManuel.isChecked()) {
            dessin.modifRef(x, y);
        }else{
            dessin.modifRefTrace(x,y);
        }


        if( capture.isChecked() || bleu){

            File file;
            file = myFiles.ouvrireFichier(fileName.getText().toString(),false);

            if(file != null) {
                String cont = myFiles.lireFile(file);
                if (cont == "File not exist") {
                    cont = "";
                }


                inZone = false;
                if( (lastpoint!=null && lastpoint.zone((float) x, (float) y,10))|| !continu){
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

                    // 10/9/20
                    long t = new Date().getTime();
                    if(bleu){
                        String desc = contenu.getText().toString();
                        myFiles.ecrireFile(file, lo + "@" + la + "#"+desc+"@time@"+t+"\n" + cont); // file IN
                        dessin.ajoutPointBleu(x, y,t,desc);
                    }else {
                        myFiles.ecrireFile(file, lo + "@" + la + "@time@"+t+"\n" + cont);
                        dessin.ajoutPointCyan(x, y,t);
                        lastpoint=new Point(x,y);
                        System.out.println("---new-last-point---"+x+" "+y);
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
        //if(nop == false){
            myLocalisation.preOnPause();
        //}
    }















}
