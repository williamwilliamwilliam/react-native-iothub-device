package com.williamwilliamwilliam.iothub;

import android.os.SystemClock;
import android.util.Log;

import com.facebook.common.internal.Sets;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Pair;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.TwinPropertyCallBack;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageCallback;
import com.microsoft.azure.sdk.iot.device.exceptions.DeviceClientException;
import com.microsoft.azure.sdk.iot.device.exceptions.TransportException;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;
import com.microsoft.azure.sdk.iot.device.transport.RetryPolicy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class IoTHubDeviceModule extends ReactContextBaseJavaModule  {

    private TwinPropertyCallBack onDesiredPropertyUpdate;

    private Gson gson = new Gson();

    @Override
    public String getName() {
        return "IoTHubDeviceModule";
    }

    public EmitHelper emitHelper = new EmitHelper();
    public ReactContext getReactContext(){
        return getReactApplicationContext();
    }

    public IoTHubDeviceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(new IoTHubLifecycleEventListener());
        onDesiredPropertyUpdate = new OnDesiredPropertyUpdate(getReactApplicationContext());
    }

    @ReactMethod
    public void sendReportedProperties(ReadableArray array, Promise promise) {
        Set<Property> propertiesToSet = new LinkedHashSet<>();
        for(int i = 0; i < array.size(); i++){
            ReadableMap map = array.getMap(i);
            String key = map.getString("key");
            Object value = getDynamicValue(map, "value");
            propertiesToSet.add(new Property(key, value));
        }
        try {
            client.sendReportedProperties(propertiesToSet);
            if(promise != null){
                promise.resolve(true);
            }
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            if(promise != null){
                promise.reject(e);
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

    private IotHubEventCallback onDeviceTwinStatusCallback(){
        return new IotHubEventCallback() {
            @Override
            public void execute(IotHubStatusCode responseStatus, Object callbackContext) {
                Log.d(this.getClass().getSimpleName(), "onDeviceTwinStatusCallback: "+responseStatus);
                WritableMap params = Arguments.createMap();
                params.putString("responseStatus", responseStatus.name());
                emitHelper.emit(getReactContext(), "onDeviceTwinStatusCallback", params);
            }
        };
    }
    private TwinPropertyCallBack onDeviceTwinPropertyRetrieved(){
        return new TwinPropertyCallBack() {
            @Override
            public void TwinPropertyCallBack(Property property, Object context) {
                Log.d(this.getClass().getSimpleName(), gson.toJson(property));
                WritableMap params = Arguments.createMap();
                params.putString("propertyJson", gson.toJson(property));
                emitHelper.emit(getReactContext(), "onDeviceTwinPropertyRetrieved", params);
            }
        };
    }

    DeviceClient client;
    IotHubEventCallback onDeviceTwinStatusCallback = onDeviceTwinStatusCallback();
    TwinPropertyCallBack twinPropertyCallBack = onDeviceTwinPropertyRetrieved();
    MessageCallback onMessageCallback = onMessageCallback();
    boolean initialized = false;
    long retryAfter = 1000;
    long retryMultiplier = 2;
    @ReactMethod
    public void connectToHub(String connectionString, ReadableArray desiredPropertySubscriptions, Promise promise) {
        try {

            client = new DeviceClient(connectionString, IotHubClientProtocol.AMQPS_WS);
            setConnectionStatusChangeCallback();
            boolean isConnectionOpened = false;
            while(!isConnectionOpened){
                try{
                    client.open();
                    isConnectionOpened = true;
                }catch(Exception e){
                    if(StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(e), "TransportException: Timed out waiting to connect to service")){
                        Log.w(this.getClass().getSimpleName(), ExceptionUtils.getRootCauseMessage(e)+". Reconnecting in "+(retryAfter/1000)+" seconds.");
                        SystemClock.sleep(retryAfter);
                        retryAfter = retryAfter* retryMultiplier;
                    }else{
                        throw e;
                    }
                }
            }

            client.startDeviceTwin(onDeviceTwinStatusCallback,
                    null,
                    twinPropertyCallBack,
                    null);
            client.setMessageCallback(onMessageCallback, null);
            subscribeToDesiredProperties(desiredPropertySubscriptions);
            initialized = true;
            promise.resolve("Successfully connected!");
        } catch (Exception e){
            String message = "There was a problem connecting to IoT Hub. "+e.getMessage();
            Log.e(this.getClass().getSimpleName(), message, e);
            promise.reject(this.getClass().getSimpleName(), e);
        }
    }

    private MessageCallback onMessageCallback() {
        return new MessageCallback() {
            @Override
            public IotHubMessageResult execute(Message message, Object callbackContext) {
                String messageString = new String(message.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET);
                Log.d(this.getClass().getSimpleName(), messageString);
                WritableMap params = Arguments.createMap();
                params.putString("message", messageString);
                params.putString("messageId", message.getMessageId());
                emitHelper.emit(getReactContext(), "onMessageReceived", params);
                return IotHubMessageResult.COMPLETE;
            }
        };
    }

    private void setConnectionStatusChangeCallback() {
        client.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallback() {
            @Override
            public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason statusChangeReason, Throwable throwable, Object callbackContext) {
                Log.d(this.getClass().getSimpleName(), "status: "+status+" reason: "+statusChangeReason);
                WritableMap params = Arguments.createMap();
                params.putString("status", status.name());
                params.putString("statusChangeReason", statusChangeReason.name());
                emitHelper.emit(getReactContext(), "onConnectionStatusChange", params);
            }
        }, null);
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


    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        return constants;
    }
}
