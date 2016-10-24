package com.example.ashik.opencvdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MAinActivity";
    private Mat imageMat;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    imageMat=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            int countWhite=0,countBlack=0;
//            for (int x = 0; x < image.getWidth() - 1; x++)
//            {
//                for (int y = 0; y < image.getHeight() - 1; y++)
//                {
//                     int color = image.getPixel(x, y);
//                    if (color == 0 && color == 0 && color == 0)
//                    {
//                        // black
//                        countBlack++;
//                    }
//                    else if (color == 255 && color == 255 && color == 255)
//                    {
//                        // white
//                        countWhite++;
//                    }
//                }
//            }
//
//            if(countBlack > countWhite){
//                Log.d("dark","dark");



        setContentView(R.layout.activity_main);
        final ImageView profile = (ImageView) findViewById(R.id.imageView);
        Picasso.with(this).load(R.drawable.dark).into(profile, new Callback() {
            @Override
            public void onSuccess() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {//You will get your bitmap here

                        Bitmap innerBitmap = ((BitmapDrawable) profile.getDrawable()).getBitmap();
                        getDominantColor(innerBitmap);
                        Log.d("dsd",Boolean.toString(isColorDark(getDominantColor(innerBitmap))));
                        Log.d("s",innerBitmap.toString());
                        getBlurredImage(innerBitmap);

                    }
                }, 100);
            }

            @Override
            public void onError() {

            }
        });

    }

    @Override
    public void onResume()
    {   super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }
    public boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        Log.d("darkness",Double.toString(darkness));
        if(darkness<0.6){
            return false; // It's a light color
        }else{
            return true; // It's a dark color
        }

    }

    public void getBlurredImage(Bitmap image){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        int l = CvType.CV_8UC1; //8-bit grey scale image
        Mat matImage = new Mat();
        Utils.bitmapToMat(image, matImage);
        Mat matImageGrey = new Mat();
        Imgproc.cvtColor(matImage, matImageGrey, Imgproc.COLOR_BGR2GRAY);

        Bitmap destImage;
        destImage = Bitmap.createBitmap(image);
        Mat dst2 = new Mat();
        Utils.bitmapToMat(destImage, dst2);
        Mat laplacianImage = new Mat();
        dst2.convertTo(laplacianImage, l);
        Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
        Mat laplacianImage8bit = new Mat();
        laplacianImage.convertTo(laplacianImage8bit, l);

        Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(), laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(laplacianImage8bit, bmp);
        int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight()); // bmp为轮廓图

        int maxLap = -16777216; // 16m
        int soglia = -6118750,darkPixels=0,totalPixels=0;
        double darkThreshold=0.5;
        boolean dark=false;

        for(int pixel : pixels){
            int color = pixel;
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            double luminance = (0.299*r+0.0f + 0.587*g+0.0f + 0.114*b+0.0f);
            if (luminance<150) {
                darkPixels++;
            }totalPixels++;
        }

        if (darkPixels/totalPixels
                >= darkThreshold) {
            dark = true;
        }
        Log.d("dark pixels",Integer.toString(darkPixels));

        Log.d("total",Integer.toString(totalPixels));

        Log.d("darkness",Boolean.toString(dark));
    }


}
