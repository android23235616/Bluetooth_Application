package com.elabs.bluetooth_application;

import android.bluetooth.BluetoothSocket;

import java.util.UUID;

/**
 * Created by Tanmay on 11-03-2017.
 */

public class Constants {
    public final UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public final static String NAME="THAT BLUETOOTH SERVICE";
    public static BluetoothSocket mainSocket;
}
