package com.elabs.bluetooth_application;

/**
 * Created by Tanmay on 10-03-2017.
 */

public class deviceDetails {

    private String name,address;

    public deviceDetails(String name, String address){
        this.name=name;
        this.address=address;
    }

    public String get_name(){
        return name;
    }

    public String get_address(){
        return address;
    }

}
