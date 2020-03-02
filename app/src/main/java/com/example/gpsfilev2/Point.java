package com.example.gpsfilev2;

public class Point {

    float x ;
    float y ;
    int couleur;
    float epaisseur;

    public Point(float x, float y, int color, float ep){
        epaisseur=ep;
        this.x=x;
        this.y=y;
        couleur=color;
    }

    public void offSet(float x0,float y0){
        this.x += x0;
        this.y += y0;
    }

    public Boolean zone(float lo, float la, float diff){
        return  Math.abs(x-lo) <  diff && Math.abs(y-la) <  diff;

    }
}
