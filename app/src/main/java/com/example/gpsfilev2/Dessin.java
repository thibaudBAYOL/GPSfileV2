package com.example.gpsfilev2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Dessin extends View {
// jlnhojb
public static final int ZONE_LIMIT = 10000;

    Float diffZone=(float)5;
    int i = 0;
    Boolean manual = true;

    int nn = 0;

    Point pointVu = null;
    Point ref = null;

    Point zoneActif=null;
    ArrayList<Point> lpActif = new ArrayList<>();
    ArrayList<Point> lp = new ArrayList<>();
    ArrayList<Point> lptempo = new ArrayList<>();

    int color= Color.BLACK;
    float epaisseur = 10;
    float echelle = 200;
    int width;
    int height;

    Paint paint = new Paint();



    public void setDiffZone(Float diffZone) {
        this.diffZone = diffZone;
    }

    public Dessin(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPointVu(Point pointVu) {
        this.pointVu = pointVu;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if(i == 0) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            i += 1;
        }
        Point p=null;

        Point lastP=null;

        float lastpX = 0;
        float lastpY = 0;
        firstRef();

        ArrayList<Point> lp_affiche;
        if( (ZONE_LIMIT -(echelle/2))<=0 ){
            lp_affiche = lp;
        }else{
            lp_affiche = lpActif;
        }

        float posX = ref.x;
        float posY = ref.y;



        nn = 0;
        float x=0;
        float y=0;

        for (int i=0; i<lp_affiche.size();i++){
            p= lp_affiche.get(i);

            if( !( (p.couleur==Color.BLACK ) && (echelle/100)>1 && (lastP!=null && p.zone(lastP.x,lastP.y,(echelle/100))) ) ){



// ?????? modifir presision zoom echelle
            paint.setStrokeWidth(p.epaisseur);
            if(i != (lp.size()-1)){
                if(p.zone(posX,posY,diffZone)){
                    paint.setColor(Color.GRAY);
                }else {
                    paint.setColor(p.couleur);//p.couleur);
                }



            }else{
                paint.setColor(Color.RED);
            }

            x= (canvas.getWidth()/2) + ((p.x - posX)*(canvas.getWidth()/echelle));
            y= (canvas.getHeight()/2) - ((p.y - posY )*(canvas.getWidth()/echelle));

            if( x >= 0 && y >= 0 && x <= canvas.getWidth() && y <= canvas.getHeight()) {
                canvas.drawPoint(x, y, paint);
                if(p.description!=""){
                    paint.setTextSize(40);
                    canvas.drawText(p.description,x,y+p.epaisseur+20,paint);
                    paint.setTextSize(20);
                }
                if(p.timer!=null && (paint.getColor()==Color.GRAY ) ) {
                    paint.setTextSize(30);
                    canvas.drawText(simpleDateFormat.format(p.timer), x, y - p.epaisseur, paint);
                    paint.setTextSize(20);
                }
                //System.out.println("------x:" + p.x + " y:" + p.y + "---DrawArtivé" + i);
                nn += 1;
            }
                if( (lastP!=null && Math.abs(lastP.x-p.x) <50 && Math.abs(lastP.y-p.y) <50) ||

               (lastP!=null && lastP.timer!=null && p.timer!=null && Math.abs(p.timer.getTime()-lastP.timer.getTime())<10000 )

                ){
                    paint.setColor(p.couleur);
                    paint.setStrokeWidth(epaisseur/3);
                    canvas.drawLine(x,y,lastpX,lastpY,paint);
                }

            // recherche sur paint et Canvas;

                lastP = p;
                lastpX = x;
                lastpY = y;

            }
        }
        lastpX=0;
        for(int j =0; j<lptempo.size();j++){
            p= lptempo.get(j);
            x= (canvas.getWidth()/2) + ((p.x - posX)*(canvas.getWidth()/echelle));
            y= (canvas.getHeight()/2) - ((p.y - posY )*(canvas.getWidth()/echelle));
            if(lastpX!=0 && Math.abs(p.timer.getTime()-lastP.timer.getTime())<10000 ) {
                paint.setColor(Color.CYAN);
                paint.setStrokeWidth(epaisseur / 3);
                canvas.drawLine(x, y, lastpX, lastpY, paint);
            }
            lastP = p;
            lastpX = x;
            lastpY = y;
        }



        if(ref != null) {
            paint.setStrokeWidth(ref.epaisseur);
            paint.setColor(ref.couleur);//p.couleur);
            canvas.drawPoint((canvas.getWidth() / 2), (canvas.getHeight() / 2), paint);
        }
        canvas.drawText(""+ nn,10,20,paint);




        if(pointVu!=null) {
            x = (canvas.getWidth() / 2) + ((pointVu.x - posX) * (canvas.getWidth() / echelle));
            y = (canvas.getHeight() / 2) - ((pointVu.y - posY) * (canvas.getWidth() / echelle));
            paint.setStrokeWidth(epaisseur*3);
            paint.setColor(pointVu.couleur);
            canvas.drawPoint(x, y, paint);
            //System.out.println("--------------------------------BBBBB"+x+"/"+ y);
        }
        //System.out.println("----------------------------------DrawArtivéFin");



    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

            firstRef();
            ref.couleur = Color.BLUE;

            int ralentire = 5;

            float diffX = ((event.getX() - (width/2))/(width/echelle) )/ralentire;
            float diffY = ((event.getY() - (height/2))/(width/echelle))/ralentire;


            // 10/9/20
            //Point ppp = new Point(ref.x+diffX,ref.y-diffY,Color.YELLOW,epaisseur/10);
            //lp.add(ppp);

            ref.offSet(diffX, -diffY);
            verification_zone_actif();
            invalidate();


        return true;
    }



    private void firstRef(){
        if(ref == null){
            float posX = 0;
            float posY = 0;
            if(lp.size() > 0) {
                Point p = lp.get(lp.size()-1);
                posX = p.x;
                posY = p.y;
            }
            ref = new Point(posX,posY,Color.CYAN,epaisseur);

            verification_zone_actif();
        }
    }


    private void verification_zone_actif(){
        if( (ZONE_LIMIT -(echelle/2))<=0 ){

        } else if(zoneActif==null || !zoneActif.zone(ref, ZONE_LIMIT-echelle)){
            zoneActif=new Point(ref);
            lpActif.clear();
             for (Point p : lp){
                 if(in_zone_actif(p))lpActif.add(p);
            }
        }//else{
            //System.out.println("------------"+ Math.max(Math.abs(zoneActif.x-ref.x), Math.abs(zoneActif.y-ref.y))+" < "+(ZONE_LIMIT - (echelle )));
        //}

    }

    private Boolean in_zone_actif(Point point){

        if( (ZONE_LIMIT -(echelle/2))<=0 ){
            return false;
        }else {
            return zoneActif.zone(point, ZONE_LIMIT );
        }
    }

    private void add_in_zone_actif(Point point){
        if(in_zone_actif(point)){
            lpActif.add(point);
        }
    }


    public void modifEchelle(float e){
        echelle = e;
        invalidate();
    }




    public boolean modifRef(int x, int y){
        Point p=new Point(x,y,Color.GREEN, epaisseur);
        modifRefTrace(x,y);
        ref = p;
        verification_zone_actif();
        invalidate ();
        return true;
    }

    // 10/9/20
    public boolean modifRefTrace(int x, int y) {
        Point ppp = new Point(x, y, Color.CYAN, epaisseur / 3,new Date().getTime());
        lptempo.add(ppp);
        if(lptempo.size()>222)lptempo.remove(0);
        return true;

    }

    // 10/9/20
    public boolean ajoutPoint(int x, int y,long t) {

        Point p=new Point(x,y,color, epaisseur,t);
        lp.add(p);

        return true;
    }

    public void refresh(){
        invalidate ();
        firstRef();
        verification_zone_actif();
    }

    // 10/9/20
    public boolean ajoutPointBleu(int x, int y,long t,String desc){
        return ajoutPointBleu(x, y,t,desc,true);
    }
    public boolean ajoutPointBleu(int x, int y,long t,String desc,Boolean refresh) {

        Point p=new Point(x,y,Color.MAGENTA, epaisseur*2,t);
        p.setDescription(desc);
        lp.add(p);

        if(refresh){
            add_in_zone_actif(p);
            invalidate ();
        }

        return true;
    }
    // 10/9/20
    public boolean ajoutPointCyan(int x, int y,long t) {

        Point p=new Point(x,y,Color.CYAN, epaisseur,t);
        lp.add(p);
        add_in_zone_actif(p);
        invalidate ();

        return true;
    }



    public void majP(float epaisseur, int c){

        System.out.println("------MAJ:");

        this.epaisseur=epaisseur;
        this.color= c;

    }





}
