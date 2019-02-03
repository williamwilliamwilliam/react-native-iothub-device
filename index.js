import {NativeEventEmitter, NativeModules} from 'react-native';
export const {IoTHubDeviceModule} = NativeModules;
/**
 * Returns Promise so you can await
 *
 * @param connectionString
 * @param desiredPropertySubscriptions
 * @param onDesiredPropertyUpdate
 * @returns {Promise}
 */
export function connectToHub(connectionString, desiredPropertySubscriptions, onDesiredPropertyUpdate){
    new NativeEventEmitter(IoTHubDeviceModule).addListener('onDesiredPropertyUpdate', (event) => {
        if(event.propertyJson){
            const property = JSON.parse(event.propertyJson);
            onDesiredPropertyUpdate(property.property);
        }
    });

    return IoTHubDeviceModule.connectToHub(connectionString, desiredPropertySubscriptions);
}
export async function subscribeToTwinDesiredProperties(propertyKey, success, failure){
    return await IoTHubDeviceModule.subscribeToTwinDesiredProperties(propertyKey, success, failure);
}

/**
 * @param {Object[]} properties - Example input: {testValue:12345, testValue2:"12345", testValue3: true}

 * @returns {Promise}
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
