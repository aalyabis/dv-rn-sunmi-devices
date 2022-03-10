import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'dv-rn-sunmi-devices' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const DvRnSunmiDevices = NativeModules.DvRnSunmiDevices
  ? NativeModules.DvRnSunmiDevices
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return DvRnSunmiDevices.multiply(a, b);
}
