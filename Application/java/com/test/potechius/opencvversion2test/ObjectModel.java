package com.test.potechius.opencvversion2test;

/**
 * Created by
 * potechius on 16.05.18.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

// a photo cube with 6 puctures on its 6 faces

public class ObjectModel {
    private FloatBuffer positions;   // vertex Buffer
    private FloatBuffer normals;   // normal Buffer
    private FloatBuffer textureCoordinates;      // texture Coords Buffer

    private Bitmap[] bitmap = new Bitmap[1];
    private int[] imageFileIDs = {  // Image file IDs
            R.drawable.chest,
    };
    int mBytesPerFloat = 4;
    int[] textureIDs = new int[1];
    int numFaces;

    Vector<Float> verts;
    float[] pos;

    // constructor - set up the vertex buffer
    public ObjectModel(Context context, String fileName){
        ObjLoader objLoader = new ObjLoader(context, fileName);
        numFaces = objLoader.numFaces;

        this.verts = objLoader.getVerts();
        this.pos = objLoader.getPositions();

        bitmap[0] = BitmapFactory.decodeStream(context.getResources().openRawResource(imageFileIDs[0]));

        // Initialize the buffers.
        // allocate vertex buffer. an float has 4 bytes
        positions = ByteBuffer.allocateDirect(objLoader.positions.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        positions.put(objLoader.positions).position(0);

        normals = ByteBuffer.allocateDirect(objLoader.normals.length * mBytesPerFloat) .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normals.put(objLoader.normals).position(0);

        textureCoordinates = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * mBytesPerFloat * objLoader.numFaces).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordinates.put(objLoader.textureCoordinates).position(0);

    }

    // render the shape
    public void draw(GL10 gl){
        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, positions);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureCoordinates);

        //gl.glDrawElements(GL10.GL_TRIANGLES, 1000, GL10.GL_UNSIGNED_BYTE, );

        gl.glPushMatrix();
        gl.glTranslatef(0f,0f,0f);
        //gl.glTranslatef(2.0f,0f,-50.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
        gl.glDrawArrays(GL10.GL_TRIANGLES,0,numFaces);
        //gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,3*1176);
        //gl.glDrawArrays(GL10.GL_POINTS,0,3*645);
        gl.glPopMatrix();

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    // Load images into 6 GL textures
    public void loadTexture(GL10 gl){
        gl.glGenTextures(1, textureIDs, 0); // Generate texture-ID array for 6 IDs

        // generate OpenGL texture images
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        // Build Texture from loaded bitmap for the currently-bind texture ID
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D,0,bitmap[0],0);
        bitmap[0].recycle();
    }

    public Vector<Float> getVerts(){
        return this.verts;
    }

    public float[] getPositions(){
        return this.pos;
    }
}
