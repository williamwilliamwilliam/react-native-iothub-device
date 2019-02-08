
# react-native-iothub-device

Develop for mobile devices using Azure IoT SDKs:
https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-how-to-develop-for-mobile-devices

Android is the only platform implemented right now. iOS is stubbed out, but doesn't do anything but log.
 - Uses AMQP over WebSockets
 - You, as connected a device, can subscribe/listen to Device Twin specific desired property changes
 - You, as connected a device, can report Device Twin Reported Properties
 
 Tested using react-native 0.58.3 starter project
 - `react-native init AwesomeProject --version react-native@0.58.3`

### Manual installation


#### Android Instructions

##### Prerequisite: configure your app as a multidex app (because Microsoft's Android SDK requires it)
android/build.gradle  
```
  defaultConfig {
     ...
    multiDexEnabled true //Add this line
  }
  
  packagingOptions {
    exclude 'thirdpartynotice.txt'
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
const joinTheInterwebOfThings = async () =>{

    // Your Connection String here
    const connectionString = 'HostName=*****************.azure-devices.net;DeviceId=********;SharedAccessKey=************';

    // Any external updates to your device's twin on these desired properties will yield a onDeviceTwinPropertyRetrieved callback
    const desiredPropertySubscriptions = ['tellMe', 'somethingGood'];

    const onConnectionStatusChange = (connectionStatus) => {
        console.log(connectionStatus); // {statusChangeReason: "CONNECTION_OK", status: "CONNECTED"}
    }
    const onDeviceTwinPropertyRetrieved = (property) => {
        console.log(property); // {isReported: false, property: {key: "tellMe", value: "who's a good device?"}, version: 4}
    }
    const onMessageReceived = (message) => {
        console.log(message); // anything
    }
    const onDeviceTwinStatusCallback = (iothubResponse) => {
        console.log(iothubResponse); // {responseStatus: "OK"}
    }

    try{
        await connectToHub(
            connectionString,
            desiredPropertySubscriptions,
            onConnectionStatusChange,
            onDeviceTwinPropertyRetrieved,
            onMessageReceived,
            onDeviceTwinStatusCallback);
    }catch(error){
        console.error(error);
    }

    try{
        console.log('reporting properties...');
        await reportProperties({
            testBoolean: true,
            testNumber: new Date().getTime(),
            testString: "here's something",
            thisPropertyWontExistOnTheTwinAnymore: null
        });
    }catch(error){
        console.error(error);
    }
}
joinTheInterwebOfThings();
```




### iOS Notes for ongoing development:

##### iOS Instructions
$ gem install cocoapods  
$ cd ios  
$ pod init



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
 
