package com.test.potechius.opencvversion2test;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import es.ava.aruco.MarkerDetector;
import es.ava.aruco.Utils;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    /**************************************************************************
     * variables
     **************************************************************************/
    private GLSurfaceView glView;
    private JavaCameraView javaCameraView;
    public Vector<Marker> detectedMarkers;
    public CameraParameters cp;
    public MarkerDetector detector;
    // conrast parameter
    float gain = 2.2f;
    // brightness parameter
    int bias = 50;
    // output = 0 -> show color image
    // output = 1 -> show grayscale image
    // output = 2 -> show threshold image
    int output = 0;
    boolean draw = false;
    private static final String TAG = "MainActivity";

    Mat mRgba, imgGray, imgThres;
    int id = -1;

    /**************************************************************************
     * Used to load the 'native-lib' library on application startup.
     **************************************************************************/
    static {
        System.loadLibrary("native-lib");
    }

    /**************************************************************************
     * ???
     **************************************************************************/
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

    /**************************************************************************
     * ???
     **************************************************************************/
    static{

    }

    /**************************************************************************
     * refresh the info view every 0.5s
     **************************************************************************/
    private void startTimerThread() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();
            int lastID = -1;
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable(){
                        public void run() {
                            if(lastID != id){
                                TextView myView = (TextView) findViewById(R.id.textView);
                                try {
                                    myView.setText(MyGLRenderer.info[id]);
                                } catch(Exception e) {
                                    Log.d(TAG, String.valueOf(e));
                                }
                            }
                            lastID = id;
                        }
                    });
                }
            }
        };
        new Thread(runnable).start();
    }

    /**************************************************************************
     * Activity method: onCreate: initialize camera and OpenGL surface
     **************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initCamera();

        initOpenGL();

        initInterface();
    }

    /**************************************************************************
     * Activity method: onPause
     **************************************************************************/
    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
        if(javaCameraView!=null)
            javaCameraView.disableView();
    }

    /**************************************************************************
     * Activity method: onDestroy
     **************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(javaCameraView!=null)
            javaCameraView.disableView();
    }

    /**************************************************************************
     * Activity method: onResume
     **************************************************************************/
    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "SUCCESS");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        } else {
            Log.d(TAG, "FAIL");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mLoaderCallBack);
        }
    }

    /**************************************************************************
     * behaviour when camera starts
     **************************************************************************/
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC1);
        imgThres = new Mat(height, width, CvType.CV_8UC1);
    }

    /**************************************************************************
     * behaviour when camera stops
     **************************************************************************/
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        imgGray.release();
        imgThres.release();
    }

    /**************************************************************************
     * input: camera image
     * output: processed camera image
     **************************************************************************/
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Imgproc.cvtColor(mRgba, imgGray, Imgproc.COLOR_RGB2GRAY);

        // binarize the image
        /*
        Imgproc.threshold(imgGray,imgThres, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        */

        // init camera and set intrinsic parameters
        cp = new CameraParameters();
        cp.setCameraMatrix();
        cp.setDistCoeff();

        detectedMarkers = new Vector<Marker>();
        detector = new MarkerDetector();

        // adapt contrast and brightness
        imgGray = Utils.changeContrastAndBrightness(imgGray, gain, bias);

        Imgproc.cvtColor(imgGray, imgGray, Imgproc.COLOR_GRAY2RGBA);

        imgThres = detector.detect(imgGray, detectedMarkers, this.cp, 500.0f, null);

        if(!detectedMarkers.isEmpty()){
            for(int i = 0; i < MyGLRenderer.markerIDs.length; i++){
                boolean found = false;
                for(int j = 0; j < detectedMarkers.size(); j++){

                    if(MyGLRenderer.markerIDs[i] == detectedMarkers.get(j).getMarkerId()){
                        id = i;
                        found = true;
                        if(draw)
                            detectedMarkers.get(j).draw(mRgba, new Scalar(0.0f,255.0f,0.0f), 2, false);
                        MyGLRenderer.rotationMatrices[i] = detectedMarkers.get(j).getRotation();
                        MyGLRenderer.translationMatrices[i] = detectedMarkers.get(j).getTranslation();
                    }
                }

                if(found == true)
                    MyGLRenderer.visible[i] = true;
                else
                    MyGLRenderer.visible[i] = false;
            }
        } else {
            MyGLRenderer.visible = new Boolean[]{false,false};
        }

        if(output == 0)
            return mRgba;
        else if(output == 1)
            return imgGray;
        else
            return imgThres;
    }

    /**************************************************************************
     * init back-camera view
     **************************************************************************/
    public void initCamera(){
        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setCameraIndex(0);
        javaCameraView.setMaxFrameSize(320  ,240);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
    }

    /**************************************************************************
     * init OpenGL
     **************************************************************************/
    public void initOpenGL(){
        glView = (GLSurfaceView) this.findViewById(R.id.glSurface);
        glView.setZOrderOnTop(true);
        glView.setEGLConfigChooser(8,8,8,8,16,0);
        glView.getHolder().setFormat(PixelFormat.RGBA_8888);
        glView.setRenderer(new MyGLRenderer(this));
    }

    /**************************************************************************
     * init interface
     **************************************************************************/
    public void initInterface(){
        SeekBar simpleSeekBar=(SeekBar) findViewById(R.id.seekBarA); // initiate the Seekbar
        simpleSeekBar.setMax(100); // 150 maximum value for the Seek bar
        simpleSeekBar.setProgress(1);

        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;

                gain = 0.1f + (float)progressChangedValue / 10.0f;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        SeekBar simpleSeekBar2=(SeekBar) findViewById(R.id.seekBarB); // initiate the Seekbar
        simpleSeekBar2.setMax(100); // 150 maximum value for the Seek bar
        simpleSeekBar2.setProgress(1);

        simpleSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;

                bias = progressChangedValue;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button upButton = (Button) findViewById(R.id.btnOriginalImage);
        upButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                output = 0;
                Log.d(TAG, "SUCCESS");
            }
        });

        Button downButton = (Button) findViewById(R.id.btnGrayImage);
        downButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                output = 1;
            }
        });

        Button thresButton = (Button) findViewById(R.id.btnThres);
        thresButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                output = 2;
            }
        });

        Button axisButton = (Button) findViewById(R.id.btnAxis);
        axisButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                draw = !draw;
            }
        });

        Button infoButton = (Button) findViewById(R.id.btnInfo);
        infoButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView myView = (TextView) findViewById(R.id.textView);
                if(myView.getVisibility() == View.INVISIBLE)
                    myView.setVisibility(View.VISIBLE);
                else
                    myView.setVisibility(View.INVISIBLE);
            }
        });

        startTimerThread();
    }

    /**************************************************************************
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     **************************************************************************/
    public native String stringFromJNI();

}

