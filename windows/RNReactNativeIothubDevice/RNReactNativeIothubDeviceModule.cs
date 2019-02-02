using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace React.Native.Iothub.Device.RNReactNativeIothubDevice
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNReactNativeIothubDeviceModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNReactNativeIothubDeviceModule"/>.
        /// </summary>
        internal RNReactNativeIothubDeviceModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNReactNativeIothubDevice";
            }
        }
    }
}
