package com.nostratic.flutter_chat_head;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class WidgetService extends Service {


    int LAYOUT_FLAG;
    View mFloatingView;

    WindowManager windowManager;
    ImageView imageClose;

    TextView tvWidget;
    float height, width;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }


        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_widget,null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        /// Initial gravity
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.x = 0;
        layoutParams.y = 100;


        /// Layout params for close button
        WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(140,
                140,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        imageParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        imageParams.y = 100;


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_baseline_close_24);
        imageClose.setVisibility(View.VISIBLE);
        windowManager.addView(imageClose,imageParams);
        windowManager.addView(mFloatingView,layoutParams);
        mFloatingView.setVisibility(View.VISIBLE);

        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();

        tvWidget = (TextView) mFloatingView.findViewById(R.id.textview);

        tvWidget.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            long startClickTime;

            int maxClickDuration= 200;



            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case  MotionEvent.ACTION_DOWN:



                        startClickTime = Calendar.getInstance().getTimeInMillis();
                        imageClose.setVisibility(View.VISIBLE);
                       Log.d("Widget =>", String.valueOf(motionEvent.getRawX()));
                        initialX = layoutParams.x;
                        initialY = layoutParams.y;

                        //touch position
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();

                        return true;
                    case  MotionEvent.ACTION_UP:
                        long clickDuration  =  Calendar.getInstance().getTimeInMillis() - startClickTime;
                        imageClose.setVisibility(View.GONE);

                        layoutParams.x =  initialX +(int) (initialTouchX-motionEvent.getRawX());
                        layoutParams.y = initialY +(int)(motionEvent.getRawY() - initialTouchY);


                        if(clickDuration<maxClickDuration){
                            Intent it = new Intent(WidgetService.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(it);

                            Toast.makeText(WidgetService.this, "time", Toast.LENGTH_SHORT).show();
                        }else{

                            if(layoutParams.y>(height*0.6)){
                                stopSelf();
                                imageClose.setVisibility(View.INVISIBLE);
                            }
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:
//                        / calculate x & y coordinate of view
                        layoutParams.x =initialX + (int)( initialTouchX - motionEvent.getRawX());
                        layoutParams.y =initialY + (int)( motionEvent.getRawY() - initialTouchY);


                        windowManager.updateViewLayout(mFloatingView, layoutParams);

                        if(layoutParams.y> (height *0.6)){
                            imageClose.setImageResource(R.drawable.ic_baseline_close_24);
                        }else{
                            imageClose.setImageResource(R.drawable.ic_baseline_close_25);
                        }

                }

                return false;

            }
        });


        return START_STICKY;

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mFloatingView!=null){
            windowManager.removeView(mFloatingView);
        }
    }
}
