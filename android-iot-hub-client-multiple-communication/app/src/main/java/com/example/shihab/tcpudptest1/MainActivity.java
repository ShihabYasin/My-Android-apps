package com.example.shihab.tcpudptest1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    protected static String GlobalServer = null;
    protected static int GlobalTcpPOrt = 0;
    protected static int GlobalUdpPOrt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setGlobalServeraddress(View view) {
        EditText serverInfoEditText = (EditText) findViewById(R.id.editText);
        GlobalServer = serverInfoEditText.getText().toString();
        //Log.d("AAAAAA","SERVER SET"+ serverInfoEditText.getText().toString() );
        Toast.makeText(this, "Server Config Done", Toast.LENGTH_SHORT).show();

        EditText tcpPortText =(EditText) findViewById(R.id.editTextTcpPort);
        EditText udpPortText =(EditText) findViewById(R.id.editTextUdpPort);
        GlobalTcpPOrt = Integer.parseInt(tcpPortText.getText().toString());
        GlobalUdpPOrt = Integer.parseInt(udpPortText.getText().toString());
    }

    public void ServerStartActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ServerActivity.class);
        startActivity(intent);
    }

    public void ClientStartActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ClientActivity.class);
        startActivity(intent);
    }
}

