package com.example.ashik.opencvdemo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MAinActivity";

//    static{
//        if(!OpenCVLoader.initDebug()){
//            Log.d(TAG,"not loaded");
//        }
//        else{
//            Log.d(TAG,"loaded");
//        }
//    }

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
        Picasso.with(this).load(R.drawable.a2m).into(profile, new Callback() {
            @Override
            public void onSuccess() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {//You will get your bitmap here

                        Bitmap innerBitmap = ((BitmapDrawable) profile.getDrawable()).getBitmap();
                        getDominantColor(innerBitmap);
                        Log.d("dsd",Boolean.toString(isColorDark(getDominantColor(innerBitmap))));
                        Log.d("s",innerBitmap.toString());
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
    {
        super.onResume();

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

}
