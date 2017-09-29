package com.elabs.bluetooth_application;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BlurMaskFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class join_activity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    BroadcastReceiver mReceiver;
    List<BluetoothDevice> devices  = new ArrayList<>();
    ProgressDialog prog;
    //recycler View
    RecyclerView mrecyclerView;
    LinearLayout linearLayout;
    RecyclerView.LayoutManager layoutManager;
    Handler handler;
    llistAdapter adapter;
    List<deviceDetails> l=new ArrayList<>();
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_activity);
        initialize();
        gettin_Available_devices();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver,filter);
        bluetoothAdapter.startDiscovery();

        adapter=new llistAdapter(getApplicationContext(),l);
        mrecyclerView.setAdapter(adapter);
    }

    private void gettin_Available_devices() {
        getPairedDevices();
       bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
              mReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Display("receiveed");
                BluetoothDevice device=null;
                String action=intent.getAction();
                if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
                {

                prog.setMessage("Searching,Please Wait!");
                   prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    prog.show();
                }
                else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                    prog.dismiss();
                    Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show();
                }
                else if(BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    //Toast.makeText(getApplicationContext(), "Sommething found", Toast.LENGTH_SHORT).show();
                   device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Toast.makeText(getApplicationContext(), device.getName().toString(), Toast.LENGTH_SHORT).show();
                    l.add(new deviceDetails(device.getName(),device.getAddress()));
                    adapter = new llistAdapter(join_activity.this,l);
                    adapter.notifyDataSetChanged();
                   mrecyclerView.setAdapter(adapter);
                    // Display(device.getName()+"\n");
                }
            }
        };
//        registerReceiver(mReceiver,filter);

    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(mReceiver);
        bluetoothAdapter.cancelDiscovery();
        finish();
    }

    private void initialize() {
        linearLayout=(LinearLayout)findViewById(R.id.linear);
        mrecyclerView=(RecyclerView)findViewById(R.id.recycler);
        prog=new ProgressDialog(this);
        layoutManager=new LinearLayoutManager(getApplicationContext(),LinearLayout.VERTICAL,false);
        mrecyclerView.setLayoutManager(layoutManager);
        handler = new Handler();
        dialog = new ProgressDialog(join_activity.this);
    }
private void Display(final String s){
    handler.post(new Runnable() {
        @Override
        public void run() {
           // Toast.makeText(join_activity.this,s,Toast.LENGTH_SHORT).show();
        }
    });
    Log.i("asd",s);
}

private void getPairedDevices(){
    Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    if(devices.size()>0){
        for(BluetoothDevice device:devices){
            l.add(new deviceDetails(device.getName(),device.getAddress()));
        }
    }
}

}
