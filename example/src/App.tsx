import * as React from 'react';

import { StyleSheet, View, Button, DeviceEventEmitter } from 'react-native';

import SunmiPrinter from 'dv-rn-sunmi-devices';
import { useEffect } from 'react';

export default function App() {

  const printHTML = async () => {

    await SunmiPrinter.printCustomHTMl("Test tisku")
      .then(res => {
        console.error(res)
      }).catch(err => {
        console.error(err)
      })
  };
  const showTwoLineText = async () => {
    await SunmiPrinter.showTwoLineText("Test","Dvou řádků")
      .then(res => {
        console.error(res)
      }).catch(err => {
        console.error(err)
      })
  };

  const writeChip = async () => {
    let data = {
      "user":"test",
      "password":"test2",
      "domain":"test3"
    }
    await SunmiPrinter.writeNFCTag(data)
      .then(res => {
        console.error(res)
      }).catch(err => {
        console.error(err)
      })
  };

  const chipLoad = async ( data: string ) =>{
    console.error(data)
  }

  useEffect(()=>{
    DeviceEventEmitter.addListener("CHIP_LOADED",chipLoad)
  },[])


  return (
    <View style={styles.container}>
      <Button title={"Tisk účtenky"} onPress={()=>printHTML()}/>
      <Button title={"2 line display test"} onPress={()=>showTwoLineText()}/>
      <Button title={"Zapsat data na čip"} onPress={()=>writeChip()}/>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection:'row',
    alignItems: 'center',
    justifyContent: 'space-around',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
