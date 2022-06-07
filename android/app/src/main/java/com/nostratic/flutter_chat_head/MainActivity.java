package com.nostratic.flutter_chat_head;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MainActivity extends FlutterActivity {
    public static Activity myDialog;
    public static boolean active = false;
    String CHANNEL = "samples.flutter.dev/battery";

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine){
    super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL).setMethodCallHandler(
                (call, result) -> {
                    // Note: this method is invoked on the main thread.

                    if(call.method.equals("notification")){
                        if(!Settings.canDrawOverlays(MainActivity.this)){
                            getPermission();
                        }else{
                            System.out.println("Data => Background service started");

                            Intent intent = new Intent( MainActivity.this, WidgetService.class);
                            intent.putExtra("close",true);

                            startService(intent);
                        }
                    }else {
                        result.notImplemented();
                    }
                }
        );

    }

    public void  getPermission(){
        // check for alert window permission
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,1);
        }

    }

    @Override
    protected void onPause() {
        if(!Settings.canDrawOverlays(MainActivity.this)){
            getPermission();
        }else{
            System.out.println("kashif 2 ");

            Intent intent = new Intent( MainActivity.this, WidgetService.class);
            intent.putExtra("close",true);

            startService(intent);
        }
        super.onPause();
        active = false;
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent( MainActivity.this, WidgetService.class);
        intent.putExtra("close",true);
        stopService(intent);
        super.onResume();
        active = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!Settings.canDrawOverlays(MainActivity.this)){
                    Toast.makeText(this, "Permission denied by user", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }




    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        active = false;
    }
}
