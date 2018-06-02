/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  es.ava.aruco.CameraParameters
 *  es.ava.aruco.Marker
 *  org.opencv.core.CvType
 *  org.opencv.core.Mat
 *  org.opencv.core.MatOfDouble
 *  org.opencv.core.MatOfPoint
 *  org.opencv.core.MatOfPoint2f
 *  org.opencv.core.Point
 *  org.opencv.core.Size
 *  org.opencv.imgproc.Imgproc
 */
package es.ava.aruco;

import es.ava.aruco.CameraParameters;
import es.ava.aruco.Marker;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MarkerDetector {
    private double thresParam1 = 7.0;
    private double thresParam2 = 7.0;
    private thresSuppMethod thresMethod = thresSuppMethod.ADPT_THRES;
    private Mat grey = new Mat();
    private Mat thres = new Mat();
    private Mat thres2 = new Mat();
    private Mat hierarchy2 = new Mat();
    private Vector<MatOfPoint> contours2 = new Vector();
    private static final double MIN_DISTANCE = 10.0;
    private static /* synthetic */ int[] $SWITCH_TABLE$es$ava$aruco$MarkerDetector$thresSuppMethod;

    public void detect(Mat in, Vector<Marker> detectedMarkers, CameraParameters cp, float markerSizeMeters, Mat frameDebug) {
        Vector<Marker> candidateMarkers = new Vector<Marker>();
        Vector<Marker> newMarkers = new Vector<Marker>();
        Imgproc.cvtColor((Mat)in, (Mat)this.grey, (int)11);
        this.thresHold(this.thresMethod, this.grey, this.thres);
        this.thres.copyTo(this.thres2);
        Imgproc.findContours((Mat)this.thres2, this.contours2, (Mat)this.hierarchy2, (int)3, (int)1);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        int i = 0;
        while (i < this.contours2.size()) {
            MatOfPoint2f contour = new MatOfPoint2f();
            this.contours2.get(i).convertTo((Mat)contour, CvType.CV_32FC2);
            int contourSize = (int)contour.total();
            if (contourSize > in.cols() / 5) {
                Imgproc.approxPolyDP((MatOfPoint2f)contour, (MatOfPoint2f)approxCurve, (double)((double)contourSize * 0.05), (boolean)true);
                if (approxCurve.total() == 4) {
                    MatOfPoint mat = new MatOfPoint();
                    approxCurve.convertTo((Mat)mat, CvType.CV_32SC2);
                    if (Imgproc.isContourConvex((MatOfPoint)mat)) {
                        double minDistFound = Double.MAX_VALUE;
                        float[] points = new float[8];
                        approxCurve.get(0, 0, points);
                        int j = 0;
                        while (j <= 4) {
                            double d = Math.sqrt((points[j] - points[(j + 2) % 4]) * (points[j] - points[(j + 2) % 4]) + (points[j + 1] - points[(j + 3) % 4]) * (points[j + 1] - points[(j + 3) % 4]));
                            if (d < minDistFound) {
                                minDistFound = d;
                            }
                            j += 2;
                        }
                        if (minDistFound > 10.0) {
                            Vector<Point> p = new Vector<Point>();
                            p.add(new Point((double)points[0], (double)points[1]));
                            p.add(new Point((double)points[2], (double)points[3]));
                            p.add(new Point((double)points[4], (double)points[5]));
                            p.add(new Point((double)points[6], (double)points[7]));
                            candidateMarkers.add(new Marker(markerSizeMeters, p));
                        }
                    }
                }
            }
            ++i;
        }
        int nCandidates = candidateMarkers.size();
        int i2 = 0;
        while (i2 < nCandidates) {
            Marker marker = (Marker)candidateMarkers.get(i2);
            List p = new Vector();
            p = marker.toList();
            double dx1 = ((Point)p.get((int)1)).x - ((Point)p.get((int)0)).x;
            double dy2 = ((Point)p.get((int)2)).y - ((Point)p.get((int)0)).y;
            double dy1 = ((Point)p.get((int)1)).y - ((Point)p.get((int)0)).y;
            double dx2 = ((Point)p.get((int)2)).x - ((Point)p.get((int)0)).x;
            double o = dx1 * dy2 - dy1 * dx2;
            if (o < 0.0) {
                Collections.swap(p, 1, 3);
                marker.setPoints(p);
            }
            ++i2;
        }
        Vector<Integer> tooNearCandidates = new Vector<Integer>();
        int i3 = 0;
        while (i3 < nCandidates) {
            Marker toMarker = (Marker)candidateMarkers.get(i3);
            List toPoints = new Vector();
            toPoints = toMarker.toList();
            int j = i3 + 1;
            while (j < nCandidates) {
                float dist = 0.0f;
                Marker fromMarker = (Marker)candidateMarkers.get(j);
                List fromPoints = new Vector();
                fromPoints = fromMarker.toList();
                dist = (float)((double)dist + Math.sqrt((((Point)fromPoints.get((int)0)).x - ((Point)toPoints.get((int)0)).x) * (((Point)fromPoints.get((int)0)).x - ((Point)toPoints.get((int)0)).x) + (((Point)fromPoints.get((int)0)).y - ((Point)toPoints.get((int)0)).y) * (((Point)fromPoints.get((int)0)).y - ((Point)toPoints.get((int)0)).y)));
                dist = (float)((double)dist + Math.sqrt((((Point)fromPoints.get((int)1)).x - ((Point)toPoints.get((int)1)).x) * (((Point)fromPoints.get((int)1)).x - ((Point)toPoints.get((int)1)).x) + (((Point)fromPoints.get((int)1)).y - ((Point)toPoints.get((int)1)).y) * (((Point)fromPoints.get((int)1)).y - ((Point)toPoints.get((int)1)).y)));
                dist = (float)((double)dist + Math.sqrt((((Point)fromPoints.get((int)2)).x - ((Point)toPoints.get((int)2)).x) * (((Point)fromPoints.get((int)2)).x - ((Point)toPoints.get((int)2)).x) + (((Point)fromPoints.get((int)2)).y - ((Point)toPoints.get((int)2)).y) * (((Point)fromPoints.get((int)2)).y - ((Point)toPoints.get((int)2)).y)));
                dist = (float)((double)dist + Math.sqrt((((Point)fromPoints.get((int)3)).x - ((Point)toPoints.get((int)3)).x) * (((Point)fromPoints.get((int)3)).x - ((Point)toPoints.get((int)3)).x) + (((Point)fromPoints.get((int)3)).y - ((Point)toPoints.get((int)3)).y) * (((Point)fromPoints.get((int)3)).y - ((Point)toPoints.get((int)3)).y)));
                if ((double)(dist /= 4.0f) < 10.0) {
                    tooNearCandidates.add(i3);
                    tooNearCandidates.add(j);
                }
                ++j;
            }
            ++i3;
        }
        Vector<Integer> toRemove = new Vector<Integer>();
        int i4 = 0;
        while (i4 < nCandidates) {
            toRemove.add(0);
            ++i4;
        }
        i4 = 0;
        while (i4 < tooNearCandidates.size()) {
            Marker first = (Marker)candidateMarkers.get((Integer)tooNearCandidates.get(i4));
            Marker second = (Marker)candidateMarkers.get((Integer)tooNearCandidates.get(i4 + 1));
            if (first.perimeter() < second.perimeter()) {
                toRemove.set((Integer)tooNearCandidates.get(i4), 1);
            } else {
                toRemove.set((Integer)tooNearCandidates.get(i4 + 1), 1);
            }
            i4 += 2;
        }
        i4 = 0;
        while (i4 < nCandidates) {
            if ((Integer)toRemove.get(i4) == 0) {
                int id;
                Marker marker = (Marker)candidateMarkers.get(i4);
                Mat canonicalMarker = new Mat();
                this.warp(in, canonicalMarker, new Size(50.0, 50.0), marker.toList());
                marker.setMat(canonicalMarker);
                marker.extractCode();
                if (marker.checkBorder() && (id = marker.calculateMarkerId()) != -1) {
                    newMarkers.add(marker);
                    Collections.rotate(marker.toList(), 4 - marker.getRotations());
                }
            }
            ++i4;
        }
        Collections.sort(newMarkers);
        toRemove.clear();
        i4 = 0;
        while (i4 < newMarkers.size()) {
            toRemove.add(0);
            ++i4;
        }
        i4 = 0;
        while (i4 < newMarkers.size() - 1) {
            if (((Marker)newMarkers.get((int)i4)).id == ((Marker)newMarkers.get((int)(i4 + 1))).id) {
                if (((Marker)newMarkers.get(i4)).perimeter() < ((Marker)newMarkers.get(i4 + 1)).perimeter()) {
                    toRemove.set(i4, 1);
                } else {
                    toRemove.set(i4 + 1, 1);
                }
            }
            ++i4;
        }
        i4 = toRemove.size() - 1;
        while (i4 >= 0) {
            if ((Integer)toRemove.get(i4) == 1) {
                newMarkers.remove(i4);
            }
            --i4;
        }
        i4 = 0;
        while (i4 < newMarkers.size()) {
            if (cp.isValid()) {
                ((Marker)newMarkers.get(i4)).calculateExtrinsics(cp.getCameraMatrix(), cp.getDistCoeff(), markerSizeMeters);
            }
            ++i4;
        }
        detectedMarkers.setSize(newMarkers.size());
        Collections.copy(detectedMarkers, newMarkers);
    }

    public void setThresholdParams(double p1, double p2) {
        this.thresParam1 = p1;
        this.thresParam2 = p2;
    }

    public double[] getThresholdParams() {
        double[] ret = new double[]{this.thresParam1, this.thresParam2};
        return ret;
    }

    public void setThresholdMethod(thresSuppMethod method) {
        this.thresMethod = method;
    }

    public thresSuppMethod getThresholdMethod() {
        return this.thresMethod;
    }

    private void thresHold(thresSuppMethod method, Mat src, Mat dst) {
        switch (MarkerDetector.$SWITCH_TABLE$es$ava$aruco$MarkerDetector$thresSuppMethod()[method.ordinal()]) {
            case 1: {
                Imgproc.threshold((Mat)src, (Mat)dst, (double)this.thresParam1, (double)255.0, (int)1);
                break;
            }
            case 2: {
                Imgproc.adaptiveThreshold((Mat)src, (Mat)dst, (double)255.0, (int)1, (int)1, (int)((int)this.thresParam1), (double)this.thresParam2);
                break;
            }
            case 3: {
                Imgproc.Canny((Mat)src, (Mat)dst, (double)10.0, (double)220.0);
            }
        }
    }

    private void warp(Mat in, Mat out, Size size, List<Point> points) {
        Mat pointsIn = new Mat(4, 1, CvType.CV_32FC2);
        Mat pointsRes = new Mat(4, 1, CvType.CV_32FC2);
        pointsIn.put(0, 0, new double[]{points.get((int)0).x, points.get((int)0).y, points.get((int)1).x, points.get((int)1).y, points.get((int)2).x, points.get((int)2).y, points.get((int)3).x, points.get((int)3).y});
        pointsRes.put(0, 0, new double[]{0.0, 0.0, size.width - 1.0, 0.0, size.width - 1.0, size.height - 1.0, 0.0, size.height - 1.0});
        Mat m = new Mat();
        m = Imgproc.getPerspectiveTransform((Mat)pointsIn, (Mat)pointsRes);
        Imgproc.warpPerspective((Mat)in, (Mat)out, (Mat)m, (Size)size);
    }

    static /* synthetic */ int[] $SWITCH_TABLE$es$ava$aruco$MarkerDetector$thresSuppMethod() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$es$ava$aruco$MarkerDetector$thresSuppMethod;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[thresSuppMethod.values().length];
        try {
            arrn[thresSuppMethod.ADPT_THRES.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[thresSuppMethod.CANNY.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        try {
            arrn[thresSuppMethod.FIXED_THRES.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {}
        $SWITCH_TABLE$es$ava$aruco$MarkerDetector$thresSuppMethod = arrn;
        return $SWITCH_TABLE$es$ava$aruco$MarkerDetector$thresSuppMethod;
    }

    private static enum thresSuppMethod {
        FIXED_THRES("FIXED_THRES", 0), 
        ADPT_THRES("ADPT_THRES", 1), 
        CANNY("CANNY", 2);
        

        private thresSuppMethod(String string2, int n2) {
        }
    }

}