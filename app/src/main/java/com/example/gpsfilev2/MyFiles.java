package com.example.gpsfilev2;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyFiles {

    boolean inOut=false;
    Context leC;

    public MyFiles(Context tt){
        leC = tt;
    }


    String lireSimple(String n){
        return lireFile(ouvrireFichier(n, inOut));
    }

    boolean existe(String name){
        return ouvrireFichier(name,inOut).exists()  ;
    }

    File ouvrireFichier(String filename, Boolean sloc){


        File director = null ;
        if( sloc == true ) {

            director = new File("/storage/self/primary/GPSfileV2");
            if (!director.exists()) {
                director.mkdir();
                System.out.println("/////////////////////////////////MKDIR ");
            }else{
                System.out.println("/////////////////////////////////EXIST ");
            }

            if (!director.exists()) {
                System.out.println("/////////////////////////////////toujour pas ");
            }
            if( !(director.canRead() & director.canWrite())){
                System.out.println("/////////////////////////////////RW ");
                //println(" read  "+director.canRead() +" write" + director.canWrite());
            }
            System.out.println("/////////////////////////////////AA ");
        }else{
            director = leC.getFilesDir();
        }

        if ( director == null){
            return null;
        }
        return new File(director, filename);

    }

    // stocage interne. // stocage externe

    void ecrireFile(File file, String fileContents ){
        ecrireFile(file,  fileContents ,false);
    }
    void ecrireFile(File file, String fileContents ,Boolean append){



        if (file == null) return;

        try (BufferedWriter writer =  new BufferedWriter(new FileWriter(file,append))) {
            writer.write(fileContents);
        } catch (IOException e) {

            //zoneOut.setText("MERDE");
        }



    }

    private String load(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        while(true) {
            String line = reader.readLine();
            if (line==null) break;
            if(!line.equals("")) {
                builder.append(line).append("\n");
            }
        }
        return builder.toString();
    }

     String lireFile(File file) {


        if (file == null) return "File not exist";

        if (!file.exists()){

            //setText("File not exist");
            System.out.println("File not exist");
            return "File not exist";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return  load(reader);
        } catch (IOException e) {
            //zoneOut.setText("MERDE2 l.108");
            System.out.println("MERDE2 l.108");
            return "File not exist";
        }
    }
    // fin





}
