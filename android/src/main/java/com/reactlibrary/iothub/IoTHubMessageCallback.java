package com.reactnativetest.iothub;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageCallback;

public class IoTHubMessageCallback implements MessageCallback{

    ReactContext reactContext;
    public IoTHubMessageCallback(ReactContext reactContext){
        super();
        this.reactContext = reactContext;
    }

    private EmitHelper emitHelper = new EmitHelper();
    public IotHubMessageResult execute(Message msg, Object context)
    {
        Log.i(this.getClass().getSimpleName(), "IoTHubMessageCallback: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

//        IoTHubDeviceModule.Counter counter = (IoTHubDeviceModule.Counter) context;
//        System.out.println(
//                "Received message " + counter.toString()
//                        + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
//
//        WritableMap params = Arguments.createMap();
//        params.putString("message", new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
//        params.putInt("message-count", counter.get());
//        emitHelper.emit(reactContext, IoTHubDeviceModule.EVENT_NAME_MESSAGE_RECEIVED, params);
//
//        counter.increment();

        return IotHubMessageResult.COMPLETE;
    }
}