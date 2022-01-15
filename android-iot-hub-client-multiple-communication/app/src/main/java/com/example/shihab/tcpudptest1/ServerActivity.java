package com.example.shihab.tcpudptest1;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

public class ServerActivity extends AppCompatActivity {

    public int tempLevelToStartAC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        EventBus.getDefault().register(this);
        ServerStart();
    }
    public void ServerStart() {
        final Server server = new Server();
        Kryo kryo = server.getKryo();
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        server.start();
        final SomeResponse ServerStrtingMessage = new SomeResponse();

        int tcpPORT = 0, udpPORT=0;

        tcpPORT = MainActivity.GlobalTcpPOrt;
        udpPORT =MainActivity.GlobalUdpPOrt;
        final Handler handler = new Handler();
        final int finalTcpPORT1 = tcpPORT;
        final int finalUdpPORT = udpPORT;
        final Runnable runnable = new Runnable() {
            public void run() {
                try {
                    server.bind(finalTcpPORT1, finalUdpPORT);
                    ServerStrtingMessage.text = "Server Starting/Refreshing on PORT: "+ finalTcpPORT1;
                    EventBus.getDefault().post(ServerStrtingMessage);
                } catch (IOException e) {
                    ServerStrtingMessage.text = "Exception on Server Starting";
                    EventBus.getDefault().post(ServerStrtingMessage  );
                    e.printStackTrace();
                }
                handler.postDelayed(this, 15000);
            }
        };
        runnable.run();

        ServerStrtingMessage.text = "Server Starting Successful";
        EventBus.getDefault().post(ServerStrtingMessage );

        final int finalTcpPORT = tcpPORT;
        server.addListener(new Listener(){
            public void connected(Connection connection){
                SomeResponse response = new SomeResponse();
                response.text = "connected";
                EventBus.getDefault().post(response);
            }

            public void received (Connection connection, Object object) {
                if (object instanceof SomeRequest) {
                    SomeRequest request = new SomeRequest();

                    if(((SomeRequest) object).text.toLowerCase().contains("Current Room Temp: ".toLowerCase()))
                    {
                        SomeResponse response = new SomeResponse();
                        int currentRoomTemp = ((SomeRequest) object).roomTemp;
                        if(currentRoomTemp > tempLevelToStartAC )
                        {
                            response.text = "On AC Now";
                        }
                        else if (currentRoomTemp< tempLevelToStartAC - 10 ){
                            response.text = "Off AC Now";
                        }else {
                            response.text = "AC Stable";
                        }
                        connection.sendTCP(response);
                    }
                    else {

                        request.text = "Client Request on PORT: " + String.valueOf(finalTcpPORT) + " =>  " + ((SomeRequest) object).text;
                        EventBus.getDefault().post(request);

                        SomeResponse response = new SomeResponse();
                        response.text = "Hello Client on PORT: " + String.valueOf(finalTcpPORT);
                        int signal = 1;
                        connection.sendTCP(response);
                        if (signal > 0) {
                            response.text = "Successful on Server Response Sending to Client on PORT: " + String.valueOf(finalTcpPORT);
                            EventBus.getDefault().post(response);
                        } else {
                            response.text = "Exception on Server Response Sending to Client on PORT: " + String.valueOf(finalTcpPORT);
                            EventBus.getDefault().post(response);
                        }
                    }
                }
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN) // Register Event Bus for SomeResponse type Object
    public void onMessageEvent(SomeResponse event) {
        Toast.makeText(getApplicationContext(), event.text, Toast.LENGTH_SHORT).show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN) // Register Event Bus for SomeRequest type Object
    public void onMessageEvent(SomeRequest event) {
        Toast.makeText(getApplicationContext(), event.text, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void setTempLevel(View view) {
        EditText tmpLimitSet = (EditText)findViewById(R.id.tempInputControl);
        tempLevelToStartAC = Integer.parseInt( tmpLimitSet.getText().toString());
        Toast.makeText(this, "AC Starting Temp Set to: "+ tempLevelToStartAC , Toast.LENGTH_SHORT).show();
    }
}
