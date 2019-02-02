
# react-native-iothub-device

Develop for mobile devices using Azure IoT SDKs:
https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-how-to-develop-for-mobile-devices

Android is the only platform implemented right now
 - Uses AMQP over WebSockets
 - You, as connected a device, can subscribe/listen to Device Twin specific desired property changes
 - You, as connected a device, can report Device Twin Reported Properties
 
 Tested using react-native 0.58.3 starter project
 - `react-native init AwesomeProject --version react-native@0.58.3`

### Manual installation

##### Prerequisite: configure your app as a multidex app (because Microsoft's Android SDK requires it)
android/build.gradle  
```
  defaultConfig {
     ...
    multiDexEnabled true //Add this line
  }
  
  dependencies {
    ...
    implementation 'com.android.support:multidex:1.0.3' //and this one
  }
```   


##### Setup this library

`$ npm install https://github.com/williamwilliamwilliam/react-native-iothub-device.git --save`

android/build.gradle  
```
 dependencies {
     ...
     implementation project(':react-native-iothub-device')
 }
 ```

android/settings.gradle 
```
include '...', 'react-native-iothub-device'
project(':react-native-iothub-device').projectDir = new File(settingsDir, '../node_modules/react-native-iothub-device/android')
 ``` 

Add to your MainApplication.java  
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

Run your app  
 - `react-native run-android`

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

