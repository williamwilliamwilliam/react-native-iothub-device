package com.reactnativetest.iothub;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.TwinPropertyCallBack;

import java.io.IOException;


public class OnDesiredPropertyUpdate implements TwinPropertyCallBack {

    private ReactContext reactContext;
    private IoTHubDeviceModule module;
    private Gson gson = new Gson();
    public OnDesiredPropertyUpdate(IoTHubDeviceModule module, ReactContext context){
        super();
        this.module = module;
        this.reactContext = context;
    }
    @Override
    public void TwinPropertyCallBack(Property property, Object context) {
        Log.i(this.getClass().getSimpleName(), gson.toJson(property));
        WritableMap params = Arguments.createMap();
        params.putString("propertyJson", gson.toJson(property));
//        if(reactContext.hasActiveCatalystInstance()){
            new EmitHelper().emit(module.getReactContext(), "onDesiredPropertyUpdate", params);
//        }else{
//            Log.w(this.getClass().getSimpleName(), "onDesiredPropertyUpdate is trying to emit, but the react context has been destroyed");
//        }
    }
}

