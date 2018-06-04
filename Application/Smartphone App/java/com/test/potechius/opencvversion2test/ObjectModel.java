package com.test.potechius.opencvversion2test;

/**
 * Created by
 * potechius on 16.05.18.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

// a photo cube with 6 puctures on its 6 faces

public class ObjectModel {

    /**************************************************************************
     * variables
     **************************************************************************/
    private FloatBuffer positions;   // vertex Buffer
    private FloatBuffer normals;   // normal Buffer
    private FloatBuffer textureCoordinates;      // texture Coords Buffer

    private Bitmap[] bitmap;
    private int[] imageFileIDs;
    int mBytesPerFloat = 4;
    int[] textureIDs;
    int numFaces;
    public List<Integer> facePosition;

    Vector<Float> verts;
    float[] pos;

    /**************************************************************************
     * constructor
     **************************************************************************/
    public ObjectModel(Context context, String fileName){
        MtlLoader mtlLoader = new MtlLoader(context, fileName);
        ObjLoader objLoader = new ObjLoader(context, fileName);

        int textureSize = mtlLoader.getMaterialList().size();
        bitmap = new Bitmap[textureSize];
        imageFileIDs = new int[textureSize];
        textureIDs = new int[textureSize];

        for(int i = 0; i < textureSize; i++){
            String textureName = mtlLoader.getMaterialList().get(i).getTextureName();
            String cut = textureName.substring(0, textureName.length()-4);
            int objID = context.getResources().getIdentifier(cut,"drawable", context.getPackageName());
            imageFileIDs[i] = objID;
            bitmap[i] = BitmapFactory.decodeStream(context.getResources().openRawResource(imageFileIDs[i]));
        }

        numFaces = objLoader.numFaces;
        facePosition = objLoader.facePosition;

        this.verts = objLoader.getVerts();
        this.pos = objLoader.getPositions();

        // Initialize the buffers.
        // allocate vertex buffer. an float has 4 bytes
        positions = ByteBuffer.allocateDirect(objLoader.positions.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        positions.put(objLoader.positions).position(0);

        normals = ByteBuffer.allocateDirect(objLoader.normals.length * mBytesPerFloat) .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normals.put(objLoader.normals).position(0);

        textureCoordinates = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * mBytesPerFloat * objLoader.numFaces).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordinates.put(objLoader.textureCoordinates).position(0);
    }

    /**************************************************************************
     * render the shape
     **************************************************************************/
    public void draw(GL10 gl){
        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, positions);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinates);

        gl.glPushMatrix();
        gl.glTranslatef(0f,0f,0f);

        for(int i = 0; i < facePosition.size(); i++){
            if(i == facePosition.size()-1) {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[i]);
                gl.glDrawArrays(GL10.GL_TRIANGLES,facePosition.get(i),numFaces-facePosition.get(i));

            } else {
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[i]);
                gl.glDrawArrays(GL10.GL_TRIANGLES,facePosition.get(i),facePosition.get(i+1)-facePosition.get(i));
            }
        }

        gl.glPopMatrix();

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    /**************************************************************************
     * load all textures in MTL
     **************************************************************************/
    public void loadTexture(GL10 gl){
        gl.glGenTextures(facePosition.size(), textureIDs, 0); // Generate texture-ID array for 6 IDs

        // generate OpenGL texture images
        for(int i = 0; i < facePosition.size(); i++){
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[i]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);


            // Build Texture from loaded bitmap for the currently-bind texture ID
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap[i],0);
            bitmap[i].recycle();
        }
    }

    /**************************************************************************
     * getter & setter
     **************************************************************************/
    public Vector<Float> getVerts(){
        return this.verts;
    }

    public float[] getPositions(){
        return this.pos;
    }
}
