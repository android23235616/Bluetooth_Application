package com.elabs.bluetooth_application;

import android.bluetooth.BluetoothSocket;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class Main2Activity extends AppCompatActivity {

    TextView mainText,player1,player2,fight,timerText;
    RelativeLayout layout;
    SoundPool soundPool;
    CountDownTimer countDownTimer;
    private int Soundid;
    private boolean SoundLoaded=false;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private static int Score=0,OScore;
    InputStream is=null;
    private Handler handler;
    private int fightSoundid;
    String mainMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Score=0;
       Initialise();
        Receive();
        timer();
        SetTypeFacea();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                SoundLoaded=true;
            }
        });
       layout.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               switch (event.getAction()){
                   case MotionEvent.ACTION_DOWN:
                      DoYourThing(event);
                       break;
                   case MotionEvent.ACTION_UP:

                       break;
               }

               return false;
           }
       });

    ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
        @Override
        public void OnShake() {
           // if (true) {
                PlaySound(Soundid);
                mainText.animate().x(generateRandom(50, 350)).y(generateRandom(150, 600)).setDuration(100);
                mainText.setText(giveString(generateRandom(35, 90)));
                mainText.setRotation(generateRandom(0, 360));
                Score++;
                player1.setText("Player 1: " + Score);
                Send_data();
            }

       // }
    });
        ShakeDetector.updateConfiguration(1.3f,1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }



    private void DoYourThing(MotionEvent event){
        PlaySound(Soundid);
        mainText.animate().x(event.getX()).y(event.getY()).setDuration(100);
        mainText.setText(giveString(generateRandom(35,90)));
        mainText.setRotation(generateRandom(0,360));
        Score++;
        player1.setText("Player 1: "+Score);
        Send_data();
    }

    private void Send_data(){
        try {
            outputStream.write((Score+"").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Display(e.toString()+"\nUnable to send Data!");
        }
    }

    private void HavingTheTimer(final int duration){
        countDownTimer = new CountDownTimer(duration,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("timer",duration/1000+"");
                timerText.setText(millisUntilFinished/1000+"");

            }

            @Override
            public void onFinish() {
                layout.setClickable(false);
                timerText.setText("OVER");
                Display("Timer is finished!");
            }
        }.start();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            outputStream.close();
            socket.close();
            ShakeDetector.destroy();

        } catch (IOException e) {
            e.printStackTrace();
            Display("Unable to close the socket!");
        }
    }

    private void Receive(){
        HavingTheTimer(10000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                         is = socket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Display(e.toString()+"\nUnable to retrieve Input Stream!");
                        try {
                            is.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            Display("Unable to close input stream");
                        }
                        break;
                    }
                    if(is!=null){
                        byte[] data = new byte[1024];
                        try {
                            is.read(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Display(e.toString()+"\nUnable to Read bytes from Input Stream!");
                            try {
                                is.close();

                            } catch (IOException e1) {
                                e1.printStackTrace();
                                Display("Unable to close input stream");
                            }
                            break;
                        }

                        try {
                             mainMsg = new String(data,"UTF-8");
                            OScore = getNumber(mainMsg);
     //                       Display(OScore+"");
                            mainMsg="Player 2: "+mainMsg;
                        }
                           catch(IOException e){
                            e.printStackTrace();
                            Display(e.toString()+"\nThe Enccoding is unsupported");
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                player2.setText(mainMsg);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private static int getNumber(String s){

        String numberOnly= s.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberOnly);
    }


    private void timer(){

        AnimateFight();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setClickable(false);
                if(OScore>Score){
                    Display("Your opponent has won");
                }else if(OScore<Score){
                    Display("Congratulations, You have won");
                }else{
                    Display("Its a draw");
                }
            }
        },10000);
    }

    private void AnimateFight(){
        Animation fade_shrink = AnimationUtils.loadAnimation(this,R.anim.fade_shrink);
        Animation heartBeat = AnimationUtils.loadAnimation(this,R.anim.heart_beat);
        timerText=(TextView)findViewById(R.id.timer);
        timerText.setAnimation(heartBeat);
        fade_shrink.setDuration(3000);
        fade_shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                PlaySound(fightSoundid);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fight.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fight.setAnimation(fade_shrink);
        PlaySound(fightSoundid);
    }

    private void Initialise(){
        fight = (TextView)findViewById(R.id.fight);
        mainText = (TextView)findViewById(R.id.mainText);
        player1 = (TextView)findViewById(R.id.player1);
        player2 = (TextView)findViewById(R.id.player2);
        player1.setText("Player 1: "+Score);
        player2.setText("Player 2: "+Score);
        layout = (RelativeLayout)findViewById(R.id.layout);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        handler = new Handler();
        Soundid = soundPool.load(this,R.raw.fork,1);
        fightSoundid = soundPool.load(this,R.raw.pop,2);
        socket = new Constants().mainSocket;
        try {
            outputStream = socket.getOutputStream();
            outputStream.write("0".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Display(e.toString()+"\nUnable to retrieve output stream.! ");
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        try {
            socket.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Display(e.toString()+"\nUnable to close bluetooth socket");
        }
    }
    private void Display(final String s){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Main2Activity.this,s,Toast.LENGTH_SHORT).show();
            }
        });
        Log.i("info",s);
    }
    private void PlaySound(final int id){
        if(SoundLoaded){
            soundPool.play(id,0.99f,0.99f,0,0,1f);
        }
    } private void PlaySound2(final int id){
        if(SoundLoaded){
            soundPool.play(id,0.99f,0.99f,1,0,1f);
        }
    }



    private void SetTypeFacea(){
        Typeface t = Typeface.createFromAsset(getAssets(),"snacker.ttf");
       // mainText.setTypeface(t);
    }

    private int  generateRandom(int Max, int Min){
        int random = (int )(Math.random() * Max+ Min);
        return random;
    }

    private String giveString(int index){
        String aString =new Character((char)index).toString();
        return aString;
    }
}
