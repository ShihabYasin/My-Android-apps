package com.example.shihab.loadwebsite;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private WebView mWebview ;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mWebview  = new WebView(this);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });


        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if( activeNetworkInfo != null && activeNetworkInfo.isConnected() )
        {

            String expire_date = "20/12/2017"; // DATE/MONTH/YEAR

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = null;
            try {
                strDate = sdf.parse(expire_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() > strDate.getTime()) {
                Toast.makeText(activity, "Usage Error: Please Contact System Admin.", Toast.LENGTH_LONG).show();
            }
            else{
                mWebview .loadUrl("http://demo.lncera.com/bsonbd/");
                setContentView(mWebview );
            }



        }
        else{
            Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }





    }


}
