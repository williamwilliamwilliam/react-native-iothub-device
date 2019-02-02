package com.reactnativetest.iothub;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;

class OnDeviceTwinStatus implements IotHubEventCallback {
    private ReactContext reactContext;
    public OnDeviceTwinStatus(ReactApplicationContext reactApplicationContext) {
        super();
        this.reactContext = reactApplicationContext;
    }

    @Override
    public void execute(IotHubStatusCode responseStatus, Object callbackContext) {
        Log.i(this.getClass().getSimpleName(), "Status: "+new Gson().toJson(responseStatus)+" Context: "+new Gson().toJson(callbackContext));
        callbackContext.notify();
    }
}
