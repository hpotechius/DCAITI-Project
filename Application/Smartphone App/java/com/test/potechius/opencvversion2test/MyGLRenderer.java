package com.test.potechius.opencvversion2test;

/**
 * Created by potechius on 16.05.18.
 */
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.content.Context;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Utils;
import es.ava.aruco.exceptions.CPException;
import es.ava.aruco.exceptions.ExtParamException;

import static android.opengl.GLES10.GL_MODELVIEW;

public class MyGLRenderer implements GLSurfaceView.Renderer{

    /**************************************************************************
     * Variables
     **************************************************************************/
    private static final String TAG="MainActivity";
    private static String[] fileNames = {"anju","chest"};
    public static Integer[] markerIDs = {256, 560};
    public static String[] info = {"INFOTEXT1", "Der „schlechteste Regisseur der Welt“ hat verkündet, mit „Rampage: President Down“ sei seine Filmkarriere beendet. Viele werden das nicht bedauern. Aber ein paar Tränen kann man ihm schon nachweinen."};
    public static Float[] scales = {10.0f,500.0f};
    public static Boolean[] visible = {false, false};
    private ObjectModel[] models;
    public static Mat[] rotationMatrices;
    public static Mat[] translationMatrices;

    /**************************************************************************
     * Contructor
     **************************************************************************/
    public MyGLRenderer(Context context) {
        models = new ObjectModel[fileNames.length];
        rotationMatrices = new Mat[fileNames.length];
        translationMatrices = new Mat[fileNames.length];

        for(int i = 0; i < fileNames.length; i++)
            models[i] = new ObjectModel(context, fileNames[i]);
    }

    /**************************************************************************
     * call back when the surface is first created or re-created
     **************************************************************************/
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);        // set colors clear-value to black

        gl.glEnable(GL10.GL_CULL_FACE);                 // ???
        gl.glClearDepthf(1.0f);                         // set depths clear-value to farthest
        gl.glEnable(GL10.GL_DEPTH_TEST);                // Enables depth-buffer for hidden surface removal
        gl.glDepthFunc(GL10.GL_LEQUAL);                 //The type of depth testing to do
        gl.glDepthMask( true );
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);                // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);                   // Disable dithering for better performance

        // Setup Texture, each time the surface is created
        for(int i = 0; i < models.length; i++)
            models[i].loadTexture(gl);

        gl.glEnable(GL10.GL_TEXTURE_2D);                // Enable texture
    }

    /**************************************************************************
     * call back after onSurfaceCreated() or whenever the windows size changes
     **************************************************************************/
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0)
            height = 1;    // to prevent divide by zero

        float aspect = (float) width / height;
        Log.d(TAG, String.valueOf(width));
        Log.d(TAG, String.valueOf(height));

        // set the viewport (display area) to cover the entire window
        gl.glViewport(0,0,width,height);

        // setup perspective projection, with aspect ration matches viewport
        gl.glMatrixMode(GL10.GL_PROJECTION);        // select projection matrix
        gl.glLoadIdentity();                        // Reset projection matrix

        CameraParameters cp = new CameraParameters();
        cp.setCameraMatrix();
        cp.setDistCoeff();

        // gnear should be greater than 0.1 -> otherwise texture edges begin to flicker
        double gnear = 10.0;
        double gfar = 10000.0;
        // get the cameraMatrix
        double[] proj_matrix = new double[16];
        float[] proj_matrixF = new float[16];

        //GLU.gluPerspective(gl,45,aspect,0.1f,100000.f);

        try {
            Utils.glGetProjectionMatrix(cp,new Size(320  ,240),new Size(320  ,240),proj_matrix,gnear,gfar);
        } catch (CPException e) {
            e.printStackTrace();
        } catch (ExtParamException e) {
            e.printStackTrace();
        }

        for(int i=0;i<16;i++){
            Double n = Double.valueOf(proj_matrix[i]);
            proj_matrixF[i] = n.floatValue();
        }

        gl.glLoadMatrixf(proj_matrixF,0);
        GLU.gluLookAt(gl,0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
        //use perspective projection

        gl.glMatrixMode(GL10.GL_MODELVIEW);         // select model-view matrix
        gl.glLoadIdentity();                        // reset
    }

    /**************************************************************************
     * call back to draw the current frame
     **************************************************************************/
    @Override
    public void onDrawFrame(GL10 gl) {
        //clear color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // ---Render the Cube ---
        gl.glMatrixMode(GL_MODELVIEW);
        //gl.glLoadIdentity();            // Reset the model-view matrix

        for(int i = 0; i < markerIDs.length; i++){
            if(visible[i] == true && rotationMatrices[i] != null && translationMatrices[i] != null){
                double[] proj_matrix = new double[16];
                float[] proj_matrixF = new float[16];
                try {
                    Utils.glGetModelViewMatrix(proj_matrix, rotationMatrices[i], translationMatrices[i]);
                } catch (ExtParamException e) {
                    e.printStackTrace();
                }
                for(int j=0;j<16;j++){
                    Double n = Double.valueOf(proj_matrix[j]);
                    proj_matrixF[j] = n.floatValue();
                }
                gl.glLoadMatrixf(proj_matrixF,0);
                gl.glRotatef( 90.0f , 1.0f, 0.0f, 0.0f); // rotate
                gl.glScalef(scales[i],scales[i],scales[i]);

                models[i].draw(gl);
            }
        }
    }
}
