package com.test.potechius.opencvversion2test;

/**
 * Created by potechius on 16.05.18.
 */
import android.app.Activity;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public final class ObjLoader {

    public final int numFaces;

    public final float[] normals;
    public final float[] textureCoordinates;
    public final float[] positions;


    public Vector<Float> verts;

    public ObjLoader(Context context, String fileName) {

        Vector<Float> vertices = new Vector<>();
        Vector<Float> normals = new Vector<>();
        Vector<Float> textures = new Vector<>();
        Vector<String> faces = new Vector<>();

        BufferedReader reader = null;
        try {
            //InputStreamReader in = new InputStreamReader(context.getAssets().open(file));
            //InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(R.raw.chest));
            int objID = context.getResources().getIdentifier("chest","raw", context.getPackageName());
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(objID));
            reader = new BufferedReader(in);

            // read file until EOF
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "v":
                        // vertices
                        vertices.add(Float.valueOf(parts[1]));
                        vertices.add(Float.valueOf(parts[2]));
                        vertices.add(Float.valueOf(parts[3]));
                        break;
                    case "vt":
                        // textures
                        textures.add(Float.valueOf(parts[1]));
                        textures.add(Float.valueOf(parts[2]));
                        break;
                    case "vn":
                        // normals
                        normals.add(Float.valueOf(parts[1]));
                        normals.add(Float.valueOf(parts[2]));
                        normals.add(Float.valueOf(parts[3]));
                        break;
                    case "f":
                        // faces: vertex/texture/normal
                        faces.add(parts[1]);
                        faces.add(parts[2]);
                        faces.add(parts[3]);
                        break;
                }
            }
        } catch (IOException e) {
            // cannot load or read file
            System.out.println("ERROR: CANNOT LOAD OR READ FILE");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        this.verts = vertices;

        System.out.println("VERTICES: " + vertices.size());
        System.out.println("NORMALS: " + normals.size());
        System.out.println("FACES: " + faces.size());
        System.out.println("TEXTURES: " + textures.size());

        numFaces = faces.size();
        this.normals = new float[numFaces * 3];
        textureCoordinates = new float[numFaces * 2];
        positions = new float[numFaces * 3];
        int positionIndex = 0;
        int normalIndex = 0;
        int textureIndex = 0;
        for (String face : faces) {
            String[] parts = face.split("/");

            int index = 3 * (Short.valueOf(parts[0]) - 1);
            positions[positionIndex++] = vertices.get(index++);
            positions[positionIndex++] = vertices.get(index++);
            positions[positionIndex++] = vertices.get(index);

            index = 2 * (Short.valueOf(parts[1]) - 1);
            textureCoordinates[normalIndex++] = textures.get(index++);
            // NOTE: Bitmap gets y-inverted
            textureCoordinates[normalIndex++] = 1 - textures.get(index);

            index = 3 * (Short.valueOf(parts[2]) - 1);
            this.normals[textureIndex++] = normals.get(index++);
            this.normals[textureIndex++] = normals.get(index++);
            this.normals[textureIndex++] = normals.get(index);
        }
    }


    public Vector<Float> getVerts(){
        return this.verts;
    }

    public float[] getPositions(){
        return this.positions;
    }
}
