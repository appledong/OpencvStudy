package com.suixinyu.opencvstudy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.suixinyu.opencvstudy.R;
import com.suixinyu.opencvstudy.permissions.PermissionsUtils;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String LOG_TAG = "dongdianzhou";

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionsUtils.verifyStoragePermissions(this);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
        textView.setOnClickListener(this);
        findViewById(R.id.textviewvision).setOnClickListener(this);
        findViewById(R.id.textviewxtx).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.textview:
                intent.setClass(getBaseContext(), ImageCommonActivity.class);
                break;
            case R.id.textviewvision:
                intent.setClass(getBaseContext(), ImageVisionOperaActivity.class);
                break;
            case R.id.textviewxtx:
                intent.setClass(getBaseContext(), ImageXtxOperaActivity.class);
                break;
        }
        startActivity(intent);
    }
}