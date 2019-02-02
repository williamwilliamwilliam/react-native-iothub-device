'use strict'

import {DeviceEventEmitter, NativeModules} from 'react-native';

export default NativeModules.IoTHubDeviceModule;
const reportedProperties = {};
export function connectToHub(connectionString, desiredPropertySubscriptions, connectionSuccess, connectionFailure, onDeviceTwinPropertyRetrieved, onDesiredPropertyUpdate){

    DeviceEventEmitter.addListener('onDesiredPropertyUpdate', (event) => {
        if(event.propertyJson){
            const property = JSON.parse(event.propertyJson);
            onDesiredPropertyUpdate(property.property);
        }
    });

    /**
     * On Android, device twin properties are retrieved one-by-one, so we
     * populate a full map in here to keep a full snapshot of the Device Twin
     * according to Azure
     */
    DeviceEventEmitter.addListener('onDeviceTwinPropertyRetrieved', (event) => {
        console.log('onDeviceTwinPropertyRetrieved');
        console.log(event);
        if(event.propertyJson){
            const property = JSON.parse(event.propertyJson);
            reportedProperties[property.property.key] = property;
            onDeviceTwinPropertyRetrieved(property.property);
        }
    });

    NativeModules.IoTHubDeviceModule.connectToHub(connectionString, desiredPropertySubscriptions, connectionSuccess, connectionFailure);
}
export function subscribeToTwinDesiredProperties(propertyKey, success, failure){
    NativeModules.IoTHubDeviceModule.subscribeToTwinDesiredProperties(propertyKey, success, failure);
}

/**onDeviceTwinPropertyRetrieved
 * {testValue:12345, testValue2:"12345", testValue3: true}
 * @param properties
 */
export function reportProperties(properties, success, failure) {
    //translate simple json map to a key/value array

    const keyValueArray = [];
    Object.keys(properties).forEach(key => {
        keyValueArray.push({
            key,
            value:properties[key]
        });
    });

    NativeModules.IoTHubDeviceModule.sendReportedProperties(keyValueArray, success, failure);
}
