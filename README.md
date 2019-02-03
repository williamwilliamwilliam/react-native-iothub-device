
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

#### iOS Instructions
$ gem install cocoapods  
$ cd ios  
$ pod init

###### these might end up being in the library instead of implemnting app

https://github.com/Azure/azure-iot-sdk-c/blob/master/iothub_client/samples/ios/CocoaPods.md  

Podfile
```
 pod 'AzureIoTHubClient', '1.2.4'  
 pod 'AzureIoTuAmqp', '1.2.4'
```

Header searcg path for Objective C usage  
```
${PODS_ROOT}/AzureIoTHubClient/inc/
${PODS_ROOT}/AzureIoTUtility/inc/
${PODS_ROOT}/AzureIoTuMqtt/inc/
${PODS_ROOT}/AzureIoTuAmqp/inc/
```
 

#### Android Instructions

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


const joinTheInterwebOfThings = async () =>{
    const connectionString = 'HostName=********.azure-devices.net;DeviceId=***********;SharedAccessKey=****************';
    const desiredPropertySubscriptions = ['tellMe', 'somethingGood'];
    const onDesiredPropertyUpdate = (property) => {
        console.log(property); // {key: "tellMe", value: "who's a good device?"}
    }

    await connectToHub(
        connectionString,
        desiredPropertySubscriptions,
        onDesiredPropertyUpdate);

    await reportProperties({
        testBoolean: true,
        testNumber: new Date().getTime(),
        testString: "here's something",
        thisPropertyWontExistOnTheTwinAnymore: null
    });
}
joinTheInterwebOfThings();
```

