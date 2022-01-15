package com.screenoff.shihab.bookamrain;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Map;


import es.dmoral.toasty.Toasty;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    /* my code */

    int maximumpageno = 10;  // Maximum Number of Book Page That Can be Accessed
    int pageno = 0;         // Book Page Starts from 0

    Button button;
    TextView pagenotextview;
    String pagestr = "";
    String str_page_no;
    String uri;
    int imageResource;
    Drawable res;

    // image zooming purpose
    public TouchImageView image;
    public DecimalFormat df;

    // To store page number last visited not using db.
    SharedPreferences.Editor editor;//= getSharedPreferences("PageLastRead", MODE_PRIVATE).edit();
    SharedPreferences prefs;// = getSharedPreferences("PageLastRead", MODE_PRIVATE);
    Map<String, Integer> map;

    /* my code */


    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.imageView1); // full-screen view


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.btnChangeImage).setOnTouchListener(mDelayHideTouchListener);

        /* my code */

        // get last saved page
        editor = getSharedPreferences("PageLastRead", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("PageLastRead", MODE_PRIVATE);
        map = (Map<String, Integer>) prefs.getAll();


        if (map.get("PageLastFocused") == null) {
            pageno = 0;
        } else {
            pageno = map.get("PageLastFocused");
        }

        //Log.d("QWERTY", "" + map.get("PageLastFocused"));

        image = (TouchImageView) findViewById(R.id.imageView1); // full-screen view

        pagenotextview = (TextView) findViewById(R.id.textViewPageno);
        str_page_no = Integer.toString(pageno);
        uri = "@drawable/page" + str_page_no;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());

        res = getResources().getDrawable(imageResource);

        addListenerOnButtonPrev();
        addListenerOnButtonNext();

        /* my code */
    }

    /* my code */

    @Override
    protected void onResume() {
        super.onResume();

        /* my code */
        uri = "@drawable/page" + str_page_no;
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());

        res = getResources().getDrawable(imageResource);

        image.setImageDrawable(res);
        pagenotextview.setText(str_page_no);

        //Toast mesg
        Toasty.info(this, "Read Translation of QURAN Everyday", Toast.LENGTH_LONG, true).show();

        // Notification
        sendNotification(this.mContentView);

    }

    /* my code */

    public void addListenerOnButtonNext() {

        image = (TouchImageView) findViewById(R.id.imageView1);
        button = (Button) findViewById(R.id.btnChangeImage);
        pagenotextview = (TextView) findViewById(R.id.textViewPageno);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (pageno == maximumpageno) {

                } else {
                    pageno++;
                    str_page_no = Integer.toString(pageno);
                    uri = "@drawable/page" + str_page_no;
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    image.setImageDrawable(res);
                    pagenotextview.setText(str_page_no);

                    editor.putInt("PageLastFocused", pageno);
                    editor.commit();

                }
            }

        });

    }

    public void addListenerOnButtonPrev() {

        image = (TouchImageView) findViewById(R.id.imageView1);
        button = (Button) findViewById(R.id.btnPrevImage);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (pageno <= 0) {

                } else {
                    pageno--;
                    str_page_no = Integer.toString(pageno);
                    uri = "@drawable/page" + str_page_no;
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);
                    image.setImageDrawable(res);
                    pagenotextview.setText(str_page_no);

                    editor.putInt("PageLastFocused", pageno);
                    editor.commit();
                }
            }

        });
    }

    /* my code */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /* my code */
    // Page selection wheeler
    public void PageSelectionFunction(View view) {

        final NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(0);
        picker.setMaxValue(maximumpageno);

        final FrameLayout layout = new FrameLayout(this);


        layout.addView(picker, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        AlertDialog.Builder aldlg = new AlertDialog.Builder(this);
        aldlg.setView(layout);
        AlertDialog.Builder builder = aldlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                pageno = picker.getValue();
                str_page_no = Integer.toString(pageno);
                uri = "@drawable/page" + str_page_no;
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());

                res = getResources().getDrawable(imageResource);

                image.setImageDrawable(res);
                pagenotextview.setText(str_page_no);

            }
        });
        aldlg.setNegativeButton(android.R.string.cancel, null);
        aldlg.show();
    }

    // App Sharing f()
    public void shareAppFunction(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = " SHARE TEXT ";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, " BOOK SUBJECT ");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Via"));
    }

    // Send Notification
    public void sendNotification(View view) {

//Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notiicon)
                        .setContentTitle("Read QURAN Translation")
                        .setContentText("Have you read QURAN today & Translation!");


// Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }
    /* my code */
}
