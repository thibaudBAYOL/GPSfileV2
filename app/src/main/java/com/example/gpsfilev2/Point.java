package com.example.gpsfilev2;

import java.util.Date;

public class Point {

    float x ;
    float y ;
    int couleur;
    float epaisseur;

    // 10/9/20
    String description;
    Date timer = null;

    public Point(Point p){
        this.x=p.x;
        this.y=p.y;
    }
    public Point(float x, float y){
        this.x=x;
        this.y=y;
    }
    public Point(float x, float y, int color, float ep,long t){
        if(t!=0){
        timer = new Date(t);
        //timer.setTime(t);
        }
        creePoint(x,y,color,ep);
    }
    public Point(float x, float y, int color, float ep){
        creePoint(x,y,color,ep);
    }

    private void creePoint(float x, float y, int color, float ep){
        epaisseur=ep;
        this.x=x;
        this.y=y;
        couleur=color;
        description="";
    }

    public void offSet(float x0,float y0){
        this.x += x0;
        this.y += y0;
    }

    public Boolean zone(float lo, float la, float diff){
        return  Math.abs(x-lo) <  diff && Math.abs(y-la) <  diff;

    }

    public Boolean zone(Point p, float diff){
        return  Math.abs(x-p.x) <  diff && Math.abs(y-p.y) <  diff;

    }

    // 10/9/20
    public void setDescription(String description) {
        this.description = description;
    }
}
