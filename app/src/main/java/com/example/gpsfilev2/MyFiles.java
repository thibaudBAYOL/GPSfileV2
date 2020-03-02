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

    Context leC;

    public MyFiles(Context tt){
        leC = tt;
    }


    String lireSimple(String n){
        return lireFile(ouvrireFichier(n, false));
    }

    boolean existe(String name){
        return ouvrireFichier(name,false) != null ;
    }

    File ouvrireFichier(String filename, Boolean sloc){


        File director = null ;
        if( sloc == true ) {

            if (Environment.DIRECTORY_DOWNLOADS.equals(Environment.getExternalStorageState())) {
                // Le périphérique est bien monté
                System.out.println("rep EXT OK ");
                director = leC.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

            } else {
                // Le périphérique n'est pas bien monté ou on ne peut écrire dessus
                System.out.println("rep EXT BAD ");
                //zoneOut.setText("rep EXT BAD ");
            }

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



        if (file == null) return;

        try (BufferedWriter writer =  new BufferedWriter(new FileWriter(file))) {
            writer.write(fileContents);
        } catch (IOException e) {

            //zoneOut.setText("MERDE");
        }

        MediaScannerConnection.scanFile(leC, new String[] {file.getPath()}, null, null);
        leC.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

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
