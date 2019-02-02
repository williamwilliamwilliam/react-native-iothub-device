
# react-native-iothub-device

Develop for mobile devices using Azure IoT SDKs:
https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-how-to-develop-for-mobile-devices

Android is the only platform implemented right now
 - Uses AMQP over WebSockets
 - You can subscribe to Device Twin desired property changes
 - You, as a device, can report Device Twin Reported Properties


## Getting started

`$ npm install react-native-iothub-device --save` (not actually published to npm yet)

### Usage

```javascript
import {connectToHub, reportProperties} from 'react-native-iothub-device';


onDesiredPropertyUpdate = (property) => {
    console.log(property);
}
onConnectionSuccess = (success) => {
    reportProperties({
        testBoolean: true,
        testNumber: new Date().getTime(),
        testString: "string",
        thisPropertyWillBeDeletedFromTheTwinBecauseNULL: null
    });
}
onConnectionFailure = (failure) => {
    console.error(failure)
}
const connectionString = 'HostName=***************.azure-devices.net;DeviceId=******************;SharedAccessKey=**********************';
const desiredPropertySubscriptions = ['tellMe', 'somethingGood'];
connectToHub(
    connectionString,
    desiredPropertySubscriptions,
    this.onConnectionSuccess,
    this.onConnectionFailure,
    this.onDeviceTwinPropertyRetrieved,
    this.onDesiredPropertyUpdate);
```

### Manual installation

`$ npm install react-native-iothub-device --save`

build.gradle  
`multiDexEnabled true`  
`implementation 'com.android.support:multidex:1.0.3'`  
`implementation project(path: ':react-native-iothub-device')`


Extend your MainApplication.java  
`public class MainApplication `***extends MultiDexApplication*** `implements ReactApplication`

Add to MainApplication.java  
`new IoTHubDevicePackage()`
