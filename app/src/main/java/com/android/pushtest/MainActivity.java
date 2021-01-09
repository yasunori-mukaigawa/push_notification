/*
  Copyright 2020 YASUNORI MUKAIGAWA LIMITED. All Rights Reserved.
 */
package com.android.pushtest;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    TextView mTokenTitle;
    TextView mTokenDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFirebase();
        setUpView();
    }

    private void setFirebase(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            String msg = getString(R.string.msg_token_fmt);
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        Log.d(TAG, token);
                        mTokenTitle.setText(R.string.token_complete);
                        mTokenDisplay.setText(token);
                    }
                });
    }

    private void setUpView(){
        mTokenTitle = findViewById(R.id.token_title);
        mTokenDisplay = findViewById(R.id.token_display);
        // Text Selection をenableにする
        mTokenDisplay.setTextIsSelectable(true);
    }
}