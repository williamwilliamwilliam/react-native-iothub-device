package com.reactnativetest.iothub;

import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;

public class IoTHubLifecycleEventListener implements LifecycleEventListener {

    @Override
    public void onHostResume() {
        Log.i(this.getClass().getSimpleName(), "onHostResume");
    }

    @Override
    public void onHostPause() {
        Log.i(this.getClass().getSimpleName(), "onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.i(this.getClass().getSimpleName(), "onHostDestroy");
    }
}
