# dv-rn-sunmi-devices

Module for controlling part of system functions of Sunmi T2 mini and P2 pro

## Installation

```sh
npm install dv-rn-sunmi-devices
```

## Usage

```js
import SunmiPrinter from 'dv-rn-sunmi-devices';
```
## Print Custom HTML
Automatically cuts paper after print

```js
const printHTML = async () => {

  await SunmiPrinter.printCustomHTMl("Test tisku")
    .then(res => {
      console.error(res)
    }).catch(err => {
      console.error(err)
    })
};
```

## Show text on 2 line display
```js
const showTwoLineText = async () => {
  await SunmiPrinter.showTwoLineText("Test","Dvou řádků")
    .then(res => {
      console.error(res)
    }).catch(err => {
      console.error(err)
    })
};

```
## Write NFC data to Tag

```js
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
```

## Event listener
For listening when Tag get nears device.
```js
const chipLoad = async ( data: string ) =>{
  console.error(data)
}

useEffect(()=>{
  DeviceEventEmitter.addListener("CHIP_LOADED",chipLoad)
},[])
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
