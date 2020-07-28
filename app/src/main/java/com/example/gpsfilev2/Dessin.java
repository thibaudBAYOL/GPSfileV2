package com.example.gpsfilev2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class Dessin extends View {
// jlnhojb
    Float diffZone=(float)2;
    int i = 0;
    Boolean manual = true;

    int nn = 0;

    Point ref = null;

    ArrayList<Point> lp = new ArrayList<>();

    int color= Color.BLACK;
    float epaisseur = 10;
    float echelle = 40;
    int width;
    int height;

    Paint paint = new Paint();

    public void setDiffZone(Float diffZone) {
        this.diffZone = diffZone;
    }

    public Dessin(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(i == 0) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            i += 1;
        }
        Point p;

        float posX = 0;
        float posY = 0;
        if(lp.size() > 0) {
            posX = lp.get(0).x;
            posY = lp.get(0).y;
        }

        if( ref != null){
            posX = ref.x;
            posY = ref.y;
        }

        nn = 0;

        for (int i=0; i<lp.size();i++){
            p= lp.get(i);
// ?????? modifir presision zoom echelle
            paint.setStrokeWidth(p.epaisseur);
            if(i != 0){
                if(p.zone(posX,posY,diffZone)){
                    paint.setColor(Color.GRAY);
                }else {
                    paint.setColor(p.couleur);//p.couleur);
                }
            }else{
                paint.setColor(Color.RED);
            }

            float x= (canvas.getWidth()/2) + ((p.x - posX)*(canvas.getWidth()/echelle));
            float y= (canvas.getHeight()/2) - ((p.y - posY )*(canvas.getWidth()/echelle));

            if( x >= 0 && y >= 0 && x <= canvas.getWidth() && y <= canvas.getHeight()) {
                canvas.drawPoint(x, y, paint);
                System.out.println("------x:" + p.x + " y:" + p.y + "---DrawArtivé" + i);
                nn += 1;
            }

            // recherche sur paint et Canvas;

        }



        if(ref != null) {
            paint.setStrokeWidth(ref.epaisseur);
            paint.setColor(ref.couleur);//p.couleur);
            canvas.drawPoint((canvas.getWidth() / 2), (canvas.getHeight() / 2), paint);
        }
        canvas.drawText(""+ nn,10,10,paint);


        System.out.println("----------------------------------DrawArtivéFin");



    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(manual) {
            if(ref == null){
                float posX = 0;
                float posY = 0;
                if(lp.size() > 0) {
                    posX = lp.get(0).x;
                    posY = lp.get(0).y;
                }
                ref = new Point(posX,posY,Color.CYAN,epaisseur);
            }else{
                ref.couleur = Color.BLUE;
            }
            int ralentire = 5;

            float diffX = ((event.getX() - (width/2))/(width/echelle) )/ralentire;
            float diffY = ((event.getY() - (height/2))/(width/echelle))/ralentire;
            System.out.println(""+width+"  "+height);


            Point ppp = new Point(ref.x+diffX,ref.y-diffY,Color.YELLOW,epaisseur/2);

            lp.add(ppp);
            ref.offSet(diffX, -diffY);

            invalidate();

        }

        return true;
    }






    public void modifEchelle(float e){
        echelle = e;
        invalidate();
    }




    public boolean modifRef(int x, int y){
        Point p=new Point(x,y,Color.GREEN, epaisseur);
        Point ppp = new Point(p.x,p.y,Color.CYAN,epaisseur/3);
        lp.add(ppp);
        ref = p;
        invalidate ();
        return true;
    }


    public boolean ajoutPoint(int x, int y) {

        Point p=new Point(x,y,color, epaisseur);
        lp.add(p);
        invalidate ();

        return true;
    }


    public boolean ajoutPointBleu(int x, int y) {

        Point p=new Point(x,y,Color.MAGENTA, epaisseur*2);
        lp.add(p);
        invalidate ();

        return true;
    }



    public void majP(float epaisseur, int c){

        System.out.println("------MAJ:");

        this.epaisseur=epaisseur;
        this.color= c;

    }





}
