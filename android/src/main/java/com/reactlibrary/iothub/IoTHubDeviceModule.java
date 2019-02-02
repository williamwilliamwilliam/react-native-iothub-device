package com.reactnativetest.iothub;

import android.util.Log;

import com.facebook.common.internal.Sets;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Pair;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.TwinPropertyCallBack;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class IoTHubDeviceModule extends ReactContextBaseJavaModule  {

    private TwinPropertyCallBack onDesiredPropertyUpdate;
    private OnDeviceTwinPropertyRetrieved onDeviceTwinPropertyRetrieved;
    private OnDeviceTwinStatus onDeviceTwinStatus;

    private Gson gson = new Gson();

    @Override
    public String getName() {
        return "IoTHubDeviceModule";
    }

    public EmitHelper emitHelper = new EmitHelper();
    public IoTHubMessageCallback messageCallback;

    public final static String EVENT_NAME_MESSAGE_RECEIVED = "onMessageReceived";

    public ReactContext getReactContext(){
        return getReactApplicationContext();
    }

    public IoTHubDeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(new IoTHubLifecycleEventListener());
        messageCallback = new IoTHubMessageCallback(reactContext);
        onDesiredPropertyUpdate = new OnDesiredPropertyUpdate(this, getReactApplicationContext());
        onDeviceTwinPropertyRetrieved = new OnDeviceTwinPropertyRetrieved(getReactApplicationContext());
        onDeviceTwinStatus = new OnDeviceTwinStatus(getReactApplicationContext());
    }

    Map<Property, Pair<TwinPropertyCallBack, Object>> desiredPropertySubscriptions = new HashMap<Property, Pair<TwinPropertyCallBack, Object>>();

    Map<String, Property> twinProperties = new HashMap<>();

    public void setTwinProperty(Property property){
        twinProperties.put(property.getKey(), property);
    }

    @ReactMethod
    public void sendReportedProperties(ReadableArray array, Callback success, Callback failure) {
        Set<Property> propertiesToSet = new LinkedHashSet<>();
        for(int i = 0; i < array.size(); i++){
            ReadableMap map = array.getMap(i);
            String key = map.getString("key");
            Object value = getDynamicValue(map, "value");
            propertiesToSet.add(new Property(key, value));
        }
        try {
            client.sendReportedProperties(propertiesToSet);
            if(success != null){
                success.invoke();
            }
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            if(failure != null){
                failure.invoke(e.getMessage());
            };
        }
    }

    @ReactMethod
    public void sendReportedProperty(ReadableMap input, Callback success, Callback failure) {
        try {
            Property property = new Property(input.getString("key"), getDynamicValue(input, "value"));
            client.sendReportedProperties(Sets.newHashSet(property));
            if(success != null){
                success.invoke();
            }
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            if(failure != null){
                failure.invoke(e.getMessage());
            }
        }
    }

    private Object getDynamicValue(ReadableMap input, String key) {
        if(input.getType(key) == ReadableType.String){
            return input.getString(key);
        } else if(input.getType(key) == ReadableType.Boolean){
            return input.getBoolean(key);
        } else if(input.getType(key) == ReadableType.Number){
            return input.getDouble(key);
        } else{
            return input.getString(key);
        }
    }

    @ReactMethod
    public void subscribeToTwinDesiredProperties(String desiredProperty, Callback success, Callback failure) {
        Log.i(this.getClass().getSimpleName(), "Subscribing to desired property "+desiredProperty);
        try{
            desiredPropertySubscriptions.put(new Property(desiredProperty, null), new Pair<TwinPropertyCallBack, Object>(onDesiredPropertyUpdate, null));

            client.subscribeToTwinDesiredProperties(desiredPropertySubscriptions);
            success.invoke();
        }catch(Exception e){
            Log.e(this.getClass().getSimpleName(), "There was a problem subscribing to a IoT Device Twin property. "+e.getMessage(), e);
            failure.invoke(e.getMessage());
        }

    }

    @ReactMethod
    public void connectToHub(String connectionString, ReadableArray desiredPropertySubscriptions, Callback success, Callback failure) {
        try {
            initializeDeviceClient(connectionString);
            client.startDeviceTwin(onDeviceTwinStatus,
                    null,
                    onDeviceTwinPropertyRetrieved,
                    null);
            subscribeToDesiredProperties(desiredPropertySubscriptions);
            success.invoke("Successfully connected!");
        } catch (Exception e){
            String message = "There was a problem connecting to IoT Hub. "+e.getMessage();
            Log.e(this.getClass().getSimpleName(), message, e);
            failure.invoke(message);
        }
    }

    private void subscribeToDesiredProperties(ReadableArray desiredPropertySubscriptions) throws IOException {
        if(desiredPropertySubscriptions == null || desiredPropertySubscriptions.size() == 0){
            return;
        }
        Map<Property, Pair<TwinPropertyCallBack, Object>> subscriptions = new HashMap<>();
        for(int i = 0; i < desiredPropertySubscriptions.size(); i++){
            subscriptions.put(new Property(desiredPropertySubscriptions.getString(i), null), new Pair<TwinPropertyCallBack, Object>(onDesiredPropertyUpdate, null));
        }
        if(!subscriptions.isEmpty()){
            client.subscribeToTwinDesiredProperties(subscriptions);
        }
    }

    DeviceClient client;
    private void initializeDeviceClient(String connectionString) throws URISyntaxException, IOException {
        if(client != null){
            Log.w(this.getClass().getSimpleName(), "Trying to connect to IoT Hub, but the Device Client already exists. Close and reopen.");
            client.closeNow();
        }
        client = new DeviceClient(connectionString, IotHubClientProtocol.AMQPS_WS);
        client.open();
    }


    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("EVENT_NAME_MESSAGE_RECEIVED", EVENT_NAME_MESSAGE_RECEIVED);
        return constants;
    }
    static class Counter
    {
        int num;

        Counter(int num) {
            this.num = num;
        }

        int get() {
            return this.num;
        }

        void increment() {
            this.num++;
        }

        @Override
        public String toString() {
            return Integer.toString(this.num);
        }
    }
}
