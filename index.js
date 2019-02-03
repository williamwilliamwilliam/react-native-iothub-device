import {NativeEventEmitter, NativeModules} from 'react-native';

/**
 * Returns Promise so you can await
 *
 * @param connectionString
 * @param desiredPropertySubscriptions
 * @param onDesiredPropertyUpdate
 * @returns {Promise}
 */
export function connectToHub(connectionString, desiredPropertySubscriptions, onDesiredPropertyUpdate){
    new NativeEventEmitter(NativeModules.IoTHubDevice).addListener('onDesiredPropertyUpdate', (event) => {
        if(event.propertyJson){
            const property = JSON.parse(event.propertyJson);
            onDesiredPropertyUpdate(property.property);
        }
    });

    return NativeModules.IoTHubDeviceModule.connectToHub(connectionString, desiredPropertySubscriptions);
}
export async function subscribeToTwinDesiredProperties(propertyKey, success, failure){
    return await NativeModules.IoTHubDeviceModule.subscribeToTwinDesiredProperties(propertyKey, success, failure);
}

/**
 * {testValue:12345, testValue2:"12345", testValue3: true}
 * @param properties
 *  * @returns {Promise}
 */
export function reportProperties(properties) {
    //translate simple json map to a key/value array

    const keyValueArray = [];
    Object.keys(properties).forEach(key => {
        keyValueArray.push({
            key,
            value:properties[key]
        });
    });

    return NativeModules.IoTHubDeviceModule.sendReportedProperties(keyValueArray);
}
