package com.elabs.bluetooth_application;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    SoundPool soundPool;
    AudioManager audioManager;
    ImageButton host, join;
    private boolean loaded=false;
    private int soundId;
    TextView app_name;
    Animation anim;
    Handler handler;
    ProgressDialog dialog;

    public static BluetoothSocket mainSocket=null;
    private static BluetoothServerSocket serverSocket=null;
    public BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialise();
        blinking();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.i("",""+sampleId);
                loaded=true;
            }
        });
        soundId = soundPool.load(this,R.raw.pop,1);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundPool();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Waiting...");
                dialog.show();

                try{
                    setUpServer();
                }catch(IOException e){
                    Display(e.toString());
                }
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundPool();
                Intent fd=new Intent(getApplicationContext(), join_activity.class);
                startActivity(fd);
                finish();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();

    }
    private void setUpServer() throws IOException{
        serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(new Constants().NAME,new Constants().uuid);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        mainSocket = serverSocket.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Display(e.toString()+"\n"+"Error setting up the server!");
                        break;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Display(e.toString()+"\nThe thread has been interrupted while sleeping!");
                        break;
                    }
                    if(mainSocket!=null){
                        new Constants().mainSocket=mainSocket;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                try {
                                    serverSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Display(e.toString()+"\nError in closing the serverSocket");
                                }finally {
                                    Display("You are connected to your friend!");
                                }
                            }
                        });
                        break;
                    }
                }
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
        }).start();
    }


    private void blinking()
    {
        anim=new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(200);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        app_name.setAnimation(anim);
    }


    private void Initialise(){
        host = (ImageButton)findViewById(R.id.host);
        join = (ImageButton)findViewById(R.id.join);
        soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        app_name= (TextView) findViewById(R.id.text);
        dialog = new ProgressDialog(MainActivity.this);
        handler = new Handler();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void playSoundPool(){
        float actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume/maxVolume;
        if(loaded){
            soundPool.play(soundId,0.99f,0.99f,0,0,1f);
        }else{
            Display("Not loaded");
        }
    }

    private void Display(final String s){
        Log.i("info",s);
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
