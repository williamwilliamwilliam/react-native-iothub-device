package com.reactnativetest.iothub;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.TwinPropertyCallBack;
import com.microsoft.azure.sdk.iot.device.Message;

public class OnDeviceTwinPropertyRetrieved implements TwinPropertyCallBack {

    private ReactContext reactContext;
    private EmitHelper emitHelper = new EmitHelper();
    private Gson gson = new Gson();
    public OnDeviceTwinPropertyRetrieved(ReactContext context){
        super();
        this.reactContext = context;
    }

    @Override
    public void TwinPropertyCallBack(Property property, Object context) {
        Log.i(this.getClass().getSimpleName(), new Gson().toJson(property));
        WritableMap params = Arguments.createMap();
        params.putString("propertyJson", gson.toJson(property));
        emitHelper.emit(reactContext, "onDeviceTwinPropertyRetrieved", params);
    }
}