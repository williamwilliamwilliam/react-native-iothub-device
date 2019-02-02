
# react-native-iothub-device

Develop for mobile devices using Azure IoT SDKs:
https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-how-to-develop-for-mobile-devices

Android is the only platform implemented right now
 - Uses AMQP over WebSockets
 - You, as connected a device, to Device Twin desired property changes
 - You, as connected a device, can report Device Twin Reported Properties


### Manual installation

`$ npm install https://github.com/williamwilliamwilliam/react-native-iothub-device.git --save`

build.gradle  
`multiDexEnabled true`  
`implementation 'com.android.support:multidex:1.0.3'`  
`implementation project(path: ':react-native-iothub-device')`

settings.gradle  
`include 'react-native-iothub-device'`  
`project(':react-native-iothub-device').projectDir = new File(settingsDir, '../node_modules/react-native-iothub-device/android')`


Extend your MainApplication.java  
`public class MainApplication `***extends MultiDexApplication*** `implements ReactApplication`

Add to MainApplication.java  
```
import com.williamwilliamwilliam.iothub.IoTHubDevicePackage;
...
    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
              new IoTHubDevicePackage()
      );
    }
```


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

