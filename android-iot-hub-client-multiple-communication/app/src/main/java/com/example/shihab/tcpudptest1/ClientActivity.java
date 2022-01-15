package com.example.shihab.tcpudptest1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

public class ClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        EventBus.getDefault().register(this);
        ClientStart();
        SeekBar sk=(SeekBar) findViewById(R.id.seekBarTemp);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView progressView = (TextView)findViewById(R.id.textViewProgress);
                progressView.setText(""+progress);
                ClientTemperatureHigh(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public void changeAcStatus(boolean checkedValue)
    {
        CheckBox chkboxACStatus = (CheckBox)findViewById(R.id.checkBox);
        chkboxACStatus.setChecked(checkedValue);
    }
    public void ClientTemperatureHigh(final int currentTemp) {
        Client client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        client.start();
        SomeResponse ClientStrtingMessage = new SomeResponse();
        ClientStrtingMessage.text = "Current Room Temp: " + currentTemp;
        EventBus.getDefault().post(ClientStrtingMessage);

        final Client FinalClient = client;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int tcpPORT =0, udpPORT = 0;

                try {

                    tcpPORT = MainActivity.GlobalTcpPOrt;
                    udpPORT = MainActivity.GlobalUdpPOrt;
                    FinalClient.connect(5000, MainActivity.GlobalServer , tcpPORT, udpPORT);
                    SomeRequest request = new SomeRequest();
                    request.text = "Current Room Temp: " + currentTemp;
                    request.roomTemp = currentTemp;
                    EventBus.getDefault().post(request);
                    FinalClient.sendTCP(request);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (Exception ee)
                {
                    SomeRequest ExceptionRequest = new SomeRequest();
                    ExceptionRequest.text = "Exception Client Request on PORT: " + String.valueOf(tcpPORT);
                    EventBus.getDefault().post(ExceptionRequest);
                    try {
                        FinalClient.reconnect();
                    } catch (IOException e) {
                        SomeRequest ExceptionRequest2 = new SomeRequest();
                        ExceptionRequest2.text =  "Reconnecting Exception on PORT => " + String.valueOf(tcpPORT);
                        EventBus.getDefault().post(ExceptionRequest2);
                        e.printStackTrace();
                    }
                }

            }
        });
        t.start();


        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof SomeResponse) {
                    SomeResponse response = new SomeResponse();
                    response.text="Server Response => ";
                    EventBus.getDefault().post(response);
                    response = (SomeResponse)object;
                    response.text = ((SomeResponse) object).text;
                    if(response.text.equals("On AC Now"))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeAcStatus(true);
                            }
                        });
                    }else if(response.text.equals("Off AC Now"))
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeAcStatus(false);
                            }
                        });
                    }
                    EventBus.getDefault().post(response);
                }
            }
        });
    }
    public void ClientStart() {
        Client client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        client.start();
        SomeResponse ClientStrtingMessage = new SomeResponse();
        ClientStrtingMessage.text = "Client Starting";
        EventBus.getDefault().post(ClientStrtingMessage);

        final Client FinalClient = client;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int tcpPORT =0, udpPORT = 0;

                try {

                    tcpPORT = MainActivity.GlobalTcpPOrt;
                    udpPORT = MainActivity.GlobalUdpPOrt;
                    FinalClient.connect(5000, MainActivity.GlobalServer , tcpPORT, udpPORT);
                    SomeRequest request = new SomeRequest();
                    request.text = "Sent to Server =>  ";
                    EventBus.getDefault().post(request);
                    request.text = "I am Client on PORT: "+tcpPORT;
                    EventBus.getDefault().post(request);
                    FinalClient.sendTCP(request);

                } catch (IOException e) {
                   e.printStackTrace();
                }
                catch (Exception ee)
                {
                    SomeRequest ExceptionRequest = new SomeRequest();
                    ExceptionRequest.text = "Exception for Client Request on PORT: " + String.valueOf(tcpPORT);
                    EventBus.getDefault().post(ExceptionRequest);
                    try {
                        FinalClient.reconnect();
                    } catch (IOException e) {
                        SomeRequest ExceptionRequest2 = new SomeRequest();
                        ExceptionRequest2.text =  "Reconnecting Exception on PORT => " + String.valueOf(tcpPORT);
                        EventBus.getDefault().post(ExceptionRequest2);
                        e.printStackTrace();
                    }
                }

            }
        });
        t.start();


        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if (object instanceof SomeResponse) {
                        SomeResponse response = new SomeResponse();
                        response.text="Server Response => ";
                        EventBus.getDefault().post(response);
                        response = (SomeResponse)object;
                        response.text = ((SomeResponse) object).text;
                        EventBus.getDefault().post(response);

                }
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SomeResponse event) {
        Toast.makeText(getApplicationContext(), event.text, Toast.LENGTH_SHORT).show();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SomeRequest event) {
        Toast.makeText(getApplicationContext(), event.text, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
