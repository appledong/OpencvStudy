package com.suixinyu.opencvstudy;

import android.app.Application;
import android.util.Log;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**
 * @author dongdz
 * @desc application实现类
 * @Data 2011-11-29
 */
public class MainApplication extends Application implements LoaderCallbackInterface {

    private final static String LOG_TAG = "dongdianzhou";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!OpenCVLoader.initDebug()) {
            Log.e(LOG_TAG, " Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,this);
        } else {
            Log.e(LOG_TAG, " OpenCV library found inside package. Using it!");
            onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onManagerConnected(int status) {
        switch (status){
            case SUCCESS:
                Log.i(LOG_TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                break;
            default:
                onManagerConnected(status);
                break;
        }
    }

    @Override
    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }
}
