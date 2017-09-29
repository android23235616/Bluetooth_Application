package com.elabs.bluetooth_application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;


public class llistAdapter extends RecyclerView.Adapter<llistAdapter.ViewHolder> {
    private Context mContext;
    private List<deviceDetails> mDBluetoothDevice;
    private SoundPool soundPool;
    private static boolean loaded=false;
    private static int soundId ;
    private AudioManager audioManager;
    private static BluetoothSocket mainSocket=null;
    private static Handler handler;
    private static BluetoothDevice device=null;
    private static BluetoothAdapter bluetoothAdapter=null;

    public llistAdapter(Context mContext, List<deviceDetails> mDBluetoothDevice) {
        this.mContext = mContext;
        this.mDBluetoothDevice = mDBluetoothDevice;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        audioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        soundId = soundPool.load(mContext,R.raw.pop,1);
        handler  = new Handler();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded=true;
            }
        });
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public llistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.cardviw,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       final deviceDetails Details = mDBluetoothDevice.get(holder.getAdapterPosition());
        holder.txt.setText(Details.get_name());
        holder.address.setText(Details.get_address());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSoundPool();
                try{
                    JoinTheServer(Details.get_address(),v);
                }catch(IOException e){
                    Display(e.toString()+"\nError in Joining the server");
                }
            }
        });
    }

    private void JoinTheServer(final String mac,View v) throws IOException{
        device = bluetoothAdapter.getRemoteDevice(mac);
        mainSocket = device.createInsecureRfcommSocketToServiceRecord(new Constants().uuid);
        mainSocket.connect();
        Display("You have beed connected");
        new Constants().mainSocket=mainSocket;
        Intent i = new Intent(v.getContext(),Main2Activity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        v.getContext().startActivity(i);
   //  mainSocket.close();
    }

    @Override
    public int getItemCount() {
        return mDBluetoothDevice.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        TextView txt,address;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView= (CardView) itemView.findViewById(R.id.cardy);
            txt= (TextView) itemView.findViewById(R.id.text_card);
            address = (TextView)itemView.findViewById(R.id.mac);
        }
    }

    private void playSoundPool(){

        if(loaded){
            soundPool.play(soundId,0.99f,0.99f,0,0,1f);
        }else{
            Display("Not loaded");
        }
    }
    private void Display(final String s){
       handler.post(new Runnable() {
           @Override
           public void run() {
               Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
           }
       });
    }
}
