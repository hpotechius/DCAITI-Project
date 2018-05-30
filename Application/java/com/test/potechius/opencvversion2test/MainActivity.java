package com.test.potechius.opencvversion2test;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import es.ava.aruco.MarkerDetector;
import es.ava.aruco.Utils;
import es.ava.aruco.exceptions.CPException;
import es.ava.aruco.exceptions.ExtParamException;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private GLSurfaceView glView;

    public static Mat rot;
    public static Mat trans;
    public static float transX = 0;
    public static float transY = 0;
    public static float transZ = 0;
    public static float rotX = 0;
    public static float rotY = 0;
    public static float rotZ = 0;

    public Vector<Marker> detectedMarkers;
    public Marker detectedMarkerTemp;
    public static float[] proj_matrix;
    ObjectModel model;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String TAG="MainActivity";
    JavaCameraView javaCameraView;
    Mat mRgba, imgGray, imgCanny, imgChrom, imgThres, imgThres2;
    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
            super.onManagerConnected(status);
        }
    };

    static{

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        detectedMarkers = new Vector<Marker>();



        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        //javaCameraView.setMaxFrameSize(1280,704);
        //javaCameraView.setMaxFrameSize(640 ,340);
        javaCameraView.setMaxFrameSize(480,320);
        //javaCameraView.setMaxFrameSize(320,240);
        //javaCameraView.setMaxFrameSize(1280  , 720);

        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());

        glView = (GLSurfaceView) this.findViewById(R.id.glSurface);

        glView.setZOrderOnTop(true);
        glView.setEGLConfigChooser(8,8,8,8,16,0);
        glView.getHolder().setFormat(PixelFormat.RGBA_8888);

        glView.setRenderer(new MyGLRenderer(this));

        model = new ObjectModel(this, "chest");

    }

    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
        if(javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(javaCameraView!=null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "SUCCESS");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "FAIL");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallBack);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC1);
        imgCanny = new Mat(height, width, CvType.CV_8UC1);
    }


    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        imgThres = new Mat();
        Imgproc.cvtColor(mRgba, imgGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(imgGray,imgThres, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        Imgproc.cvtColor(imgThres, imgThres, Imgproc.COLOR_GRAY2RGBA);
        //Imgproc.Canny(imgGray, imgCanny, 50, 150);

        CameraParameters cp = new CameraParameters();
        MarkerDetector detector = new MarkerDetector();
        cp.setCameraMatrix();
        cp.setDistCoeff();

        //String path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.camera).toString();
        //cp.readFromXML(path);
        //Log.d(TAG, path);
        Log.d(TAG, String.valueOf(imgThres.channels()));
        detector.detect(imgThres, detectedMarkers, cp, 500.0f, null);



        if(!detectedMarkers.isEmpty()){

            //detectedMarkerTemp = new Marker(500.0f, detectedMarkers.get(0).getPoints());
            //detectedMarkerTemp.calculateExtrinsics(cp.getCameraMatrix(), cp.getDistCoeff(), 500.0f);

            //Mat cameraMatrix = new Mat( 3, 3, CvType.CV_64FC1 );
            //int row = 0, col = 0;
            //cameraMatrix.put(row ,col, 1.2519588293098975e+03, 0., 6.6684948780852471e+02, 0., 1.2519588293098975e+03 ,3.6298123112613683e+02 ,0., 0., 1.);

            //detectedMarkers.get(0).draw(mRgba, new Scalar(0.0f,255.0f,0.0f), 2, true);
            //detectedMarkers.get(0).draw3dCube(mRgba, cp, new Scalar(0.0f,155.0f,0.0f));
            //detectedMarkers.get(0).draw3dAxis(mRgba, cp, new Scalar(0.0f,0.0f,255.0f));
            //detectedMarkers.get(0).draw3dWireframe(mRgba, cp, new Scalar(255.0f,255.0f,255.0f), model);
            rot = detectedMarkers.get(0).getRotation();
            trans = detectedMarkers.get(0).getTranslation();
            transX = (float)detectedMarkers.get(0).getTranslation().get(0,0)[0];
            transY = (float)detectedMarkers.get(0).getTranslation().get(1,0)[0];
            transZ = (float)detectedMarkers.get(0).getTranslation().get(2,0)[0];
            rotX = (float)detectedMarkers.get(0).getRotation().get(0,0)[0];
            rotY = (float)detectedMarkers.get(0).getRotation().get(1,0)[0];
            rotZ = (float)detectedMarkers.get(0).getRotation().get(2,0)[0];

            // draw second marker
            /*if(detectedMarkers.size() > 1){
                detectedMarkers.get(1).draw(mRgba, new Scalar(150.0f,150.0f,150.0f), 2, true);
            }*/
            //Log.d(TAG, "Rotation: " + detectedMarkers.get(0).getRotations());

        }


        //System.out.println("Detected Marker Size: " + detectedMarkers.size());
        //Log.d(TAG, "Detected Marker Size: " + detectedMarkers.size());

        return mRgba;
    }
}

