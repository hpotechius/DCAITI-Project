package com.test.potechius.opencvversion2test;

/**
 * Created by potechius on 16.05.18.
 */
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.content.Context;
import android.util.Log;

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
    private ObjectModel model;
    private String fileName = "chest";

    /**************************************************************************
     * Contructor
     **************************************************************************/
    public MyGLRenderer(Context context) {
        model = new ObjectModel(context, fileName);
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
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // nice perspective view
        gl.glShadeModel(GL10.GL_SMOOTH);                // Enable smooth shading of color
        gl.glDisable(GL10.GL_DITHER);                   // Disable dithering for better performance

        // Setup Texture, each time the surface is created
        model.loadTexture(gl);              // load images into textures
        gl.glEnable(GL10.GL_TEXTURE_2D);                // Enable texture
    }

    /**************************************************************************
     * call back after onSurfaceCreated() or whenever the windows size changes
     **************************************************************************/
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;    // to prevent divide by zero

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
        double gnear = 0.1;
        double gfar = 100000.0;
        // get the cameraMatrix
        double[] proj_matrix = new double[16];
        float[] proj_matrixF = new float[16];

        //GLU.gluPerspective(gl,45,aspect,0.1f,100000.f);

        try {
            Utils.glGetProjectionMatrix(cp,new Size(480,320),new Size(480,320),proj_matrix,gnear,gfar);
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

        // You OpenGL/ES display re-sizing code here
    }

    private final float[] mViewMatrix = new float[16];

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

        //gl.glRotatef(angleCube, 0.0f, 1.0f, 0.0f); // rotate

        //gl.glRotatef((float)Math.toDegrees(MainActivity.rotX), 0.0f, 0.0f, 1.0f); // rotate

        //float roZ = (float)(Math.toDegrees(MainActivity.rotZ) / 80.0f * 180.0f);
        //gl.glRotatef(roZ, 0.0f, 0.0f, 1.0f); // rotate

        //float ro = -(float)(Math.toDegrees(MainActivity.rotY) / 163.0f * 180.0f);
        //gl.glRotatef( ro , 0.0f, 1.0f, 0.0f); // rotate

        //gl.glRotatef((float)Math.toDegrees(MainActivity.rotZ), 0.0f, 0.0f, 1.0f); // rotate

        /*gl.glRotatef((float)Math.toDegrees(MainActivity.rotX), 0.0f, 0.0f, 1.0f); // rotate
        gl.glRotatef((float)Math.toDegrees(MainActivity.rotY), 0.0f, 1.0f, 0.0f); // rotate
        gl.glRotatef((float)Math.toDegrees(MainActivity.rotZ), 1.0f, 0.0f, 0.0f); // rotate*/

        //gl.glRotatef( -90.0f , 1.0f, 0.0f, 0.0f); // rotate

        /*float cz = (float) Math.cos(MainActivity.rotX);
        float cy = (float) Math.cos(MainActivity.rotY);
        float cx = (float) Math.cos(MainActivity.rotZ);
        float sz = (float) Math.sin(MainActivity.rotX);
        float sy = (float) Math.sin(MainActivity.rotY);
        float sx = (float) Math.sin(MainActivity.rotZ);*/

        /*float rotationMatrix[] =
                {cy*cz , sx*sy*cz+cx*sz , -sy*cx*cz+sx*sz , 0.0f,
                -cy*sz , -sx*sy*sz+cx*cz  , sy*cx*sz+sx*cz  , 0.0f,
                sy     , -sx*cy          , cx*cy          , 0.0f,
                 MainActivity.transX   , MainActivity.transY            ,  -MainActivity.transZ       , 1.0f};*/

        /*float rotationMatrix[] =
                {cy*cz , sx*sy*cz-cx*sz , sy*cx*cz+sx*sz , 0.0f,
                cy*sz , sx*sy*sz+cx*cz  , sy*cx*sz-sx*cz  , 0.0f,
                -sy     , sx*cy          , cx*cy          , 0.0f,
                 MainActivity.transX   , MainActivity.transY            ,  -MainActivity.transZ       , 1.0f};*/


        if(MainActivity.rot != null && MainActivity.trans != null){
            double[] proj_matrix = new double[16];
            float[] proj_matrixF = new float[16];
            try {
                Utils.glGetModelViewMatrix(proj_matrix, MainActivity.rot, MainActivity.trans);
            } catch (ExtParamException e) {
                e.printStackTrace();
            }
            for(int i=0;i<16;i++){
                Double n = Double.valueOf(proj_matrix[i]);
                proj_matrixF[i] = n.floatValue();
            }
            gl.glLoadMatrixf(proj_matrixF,0);
        }

        gl.glRotatef( 90.0f , 1.0f, 0.0f, 0.0f); // rotate
        gl.glScalef(600.0f,600.0f,600.0f);

        model.draw(gl);
    }
}
