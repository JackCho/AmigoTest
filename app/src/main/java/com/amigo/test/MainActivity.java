package com.amigo.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import me.ele.amigo.Amigo;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String APK_NAME = "amigo_test_patch.apk";
    
    private TextView mTextView;
    private Button mButton1;
    private Button mButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextView = (TextView) findViewById(R.id.tv_text);
        String text = "version code: " + BuildConfig.VERSION_CODE;
        text += "\n";
        text += "version code from host-->" + Amigo.getHostPackageInfo(this, 0).versionCode;
        mTextView.setText(text);
        
        mButton1 = (Button) findViewById(R.id.btn_button1);
        mButton2 = (Button) findViewById(R.id.btn_button2);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_button1:
                applyPatchApkImmediately();
                break;
            
            case R.id.btn_button2:
                applyPatchApkLater();
                break;
        }
    }

    public void applyPatchApkImmediately() {
        File dir = Environment.getExternalStorageDirectory();
        File patchApkFile = new File(dir, APK_NAME);
        Log.i(TAG, "applyPatchApkImmediately: "+patchApkFile.getAbsolutePath());
        if (!patchApkFile.exists()) {
            Toast.makeText(this,
                    "No amigo_patch.apk found in the directory: " + dir.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        boolean patchWorked = Amigo.hasWorked();
        if (!patchWorked) {
            Amigo.work(this, patchApkFile);
            return;
        }
        Amigo.work(this, patchApkFile);
    }

    public void applyPatchApkLater() {
        File dir = Environment.getExternalStorageDirectory();
        File patchApkFile = new File(dir, APK_NAME);
        Log.i(TAG, "applyPatchApkLater: "+patchApkFile.getAbsolutePath());
        if (!patchApkFile.exists()) {
            Toast.makeText(this,
                    "No amigo_patch.apk found in the directory: " + dir.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        boolean patchWorked = Amigo.hasWorked();
        if (!patchWorked) {
            Amigo.workLater(this, patchApkFile, new Amigo.WorkLaterCallback() {
                @Override
                public void onPatchApkReleased() {
                    Toast.makeText(getApplicationContext(), "dex opt done!", Toast.LENGTH_SHORT).show();
                }
            });
            Toast.makeText(this,
                    "waiting for seconds, and kill this app and relaunch the app to check result",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Amigo.workLater(this, patchApkFile, new Amigo.WorkLaterCallback() {
            @Override
            public void onPatchApkReleased() {
                Toast.makeText(getApplicationContext(), "dex opt done!", Toast.LENGTH_SHORT).show();
            }
        });
        Toast.makeText(this,
                "waiting for seconds, and kill this app and relaunch the app to check result",
                Toast.LENGTH_SHORT).show();
    }
}
