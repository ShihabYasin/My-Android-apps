package com.example.shihab.RESTART;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static final String LOG_TAG = "ScreenOffActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG, "onCreate");

        try {
            Process process = Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}