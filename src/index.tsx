import { NativeModules} from 'react-native';

const { DvRnSunmiDevices} = NativeModules;

type SunmiPrinter ={
  printCustomHTMl: (htmlToConvert: string) => Promise<boolean>;
  showTwoLineText: (firstRow: string, secondRow: string) => Promise<boolean>;
  writeNFCTag: (data: { user: string; password: string; domain: string }) => Promise<boolean>;
  CHIP_EVENT: "CHIP_LOADED"
}

export default DvRnSunmiDevices as SunmiPrinter;


