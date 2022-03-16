/*
 * Copyright (c) 2021. DEEP VISION s.r.o.
 * Author: Lukáš Outlý
 * Project: Speedlo POS
 */

package com.dvrnsunmidevices.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.dvrnsunmidevices.BaseApp;
import com.dvrnsunmidevices.callback.PrinterCallback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;
import java.util.List;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;


public class SunmiUtil {
  private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
  private static final String SERVICE＿ACTION = "woyou.aidlservice.jiuiv5.IWoyouService";
  private static final SunmiUtil mSunmiUtil = new SunmiUtil();
  private Promise promise;
  private IWoyouService woyouService;
  private Context context;

  private final ServiceConnection connService = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
      woyouService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      woyouService = IWoyouService.Stub.asInterface(service);
    }
  };

  private PrinterCallback callback = new PrinterCallback() {
    @Override
    public String getResult() {
      return null;
    }

    @Override
    public void onReturnString(String result) {
      System.out.println("RESULT: " + result);

    }
  };

  private SunmiUtil() {
  }

  public static SunmiUtil getInstance() {
    return mSunmiUtil;
  }


  public void connectPrinterService(ReactApplicationContext context) {
    this.context = context.getApplicationContext();
    Intent intent = new Intent();
    intent.setPackage(SERVICE＿PACKAGE);
    intent.setAction(SERVICE＿ACTION);
    context.getApplicationContext().startService(intent);
    context.getApplicationContext().bindService(intent, connService, Context.BIND_AUTO_CREATE);
  }


  public void disconnectPrinterService(Context context) {
    if (woyouService != null) {
      context.getApplicationContext().unbindService(connService);
      woyouService = null;
    }
  }

  public ICallback generateCB() {
    return new ICallback.Stub() {

      @Override
      public void onRunResult(boolean isSuccess) {
        Log.d(BaseApp.APP_TAG, "Print callback success " + isSuccess);
        promise.resolve("Bitmapa vytisknuta");
      }

      @Override
      public void onReturnString(String result) {
        Log.d(BaseApp.APP_TAG, "Print callback return " + result);
      }

      @Override
      public void onRaiseException(int code, String msg) {
        Log.e(BaseApp.APP_TAG, "Callback exception  " + code + " " + msg);
        promise.reject(String.valueOf(code), msg);

      }

      @Override
      public void onPrintResult(int code, String msg) {
        Log.d(BaseApp.APP_TAG, "Print callback  result" + code + " " + msg);
      }
    };
  }

  public void initPrinter() {
    if (woyouService == null) {
      promise.reject("Woyou service not initialized");
      //Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show();
      return;
    }

    try {
      woyouService.printerInit(null);
    } catch (RemoteException e) {
      e.printStackTrace();
      promise.reject(e);

    }
  }


  public void printQr(String data, int modulesize, int errorlevel) {
    if (woyouService == null) {
      promise.reject("Woyou service not initialized");
      //   Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show();
      return;
    }

    try {
      woyouService.setAlignment(1, null);
      woyouService.printQRCode(data, modulesize, errorlevel, null);
      woyouService.lineWrap(3, null);
    } catch (RemoteException e) {
      e.printStackTrace();
      promise.resolve(e);
    }
  }


  public void printBitmapSplit(Bitmap bitmap) {
    if (woyouService == null) {
      promise.reject("Woyou service not initialized");
      // Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show();
      return;
    }

    List<Bitmap> bitmaps = new ArrayList<>();

    int x = 0;
    int y = 0;
    int height = 1024;
    int bitmapCount = (int) Math.ceil(bitmap.getHeight() / (double) height);

    for (int i = 0; i < bitmapCount; i++) {
      y = height * i;
      if (bitmap.getHeight() < height) height = bitmap.getHeight();
      else if (bitmap.getHeight() < height + y) height = (bitmap.getHeight() - y);
      Bitmap btmp = Bitmap.createBitmap(bitmap, x, y, bitmap.getWidth(), height);
      bitmaps.add(btmp);
    }

    try {
      woyouService.printerInit(generateCB());
      woyouService.setAlignment(1, null);
      int i = 0;
      for (Bitmap bitmapSplit : bitmaps) {
        woyouService.printBitmap(bitmapSplit, generateCB());
        Thread.sleep(150);

      }
      woyouService.lineWrap(1, null);
    } catch (RemoteException e) {
      e.printStackTrace();
      promise.resolve(e);
    } catch (InterruptedException e) {
      e.printStackTrace();
      promise.resolve(e);
    }
  }

  public void print3Line() {
    if (woyouService == null) {
      promise.reject("Woyou service not initialized");
      //Toast.makeText(context, R.string.toast_2, Toast.LENGTH_LONG).show();
      return;
    }

    try {
      woyouService.lineWrap(3, null);
    } catch (RemoteException e) {
      promise.reject(e);
      e.printStackTrace();
    }
  }

  public void cutPaper(PrinterCallback callback) {
    if (woyouService == null) {
      promise.reject("Woyou service not initialized");
      //Toast.makeText(context, R.string.toast_2, Toast.LENGTH_SHORT).show();
      //UIUtil.showNotification((AppCompatActivity) context,context.getString(R.string.toast_2));
      return;
    }

    try {
      woyouService.cutPaper(generateCB());
    } catch (RemoteException e) {
      promise.reject(e);
      e.printStackTrace();
    }
  }

  public void show2LineText(String line1, String line2) {
    if (woyouService == null) {
      //Toast.makeText(context, R.string.toast_2, Toast.LENGTH_SHORT).show();
      //UIUtil.showNotification((AppCompatActivity) context,context.getString(R.string.toast_2));
      promise.reject("Woyou service not initialized");

      return;
    }

    try {
      woyouService.sendLCDCommand(1);
      woyouService.sendLCDCommand(2);
      woyouService.sendLCDCommand(4);
      woyouService.sendLCDDoubleString(line1, line2, null);
    } catch (RemoteException e) {
      promise.reject(e);
      e.printStackTrace();
    }
  }

  public void openDrawer() {
    if (woyouService == null) {
      promise.reject("Woyou service not initialized");
      return;
    }
    try {
      woyouService.openDrawer(generateCB());
    } catch (RemoteException e) {
      promise.reject(e);
      e.printStackTrace();
    }
  }

  /*
    public void showBitmap(Bitmap image) {
      if (woyouService == null) {
        return;
      }
      try {
        woyouService.sendLCDBitmap(image, null);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  */
  public void passCallback(Promise promise) {
    this.promise = promise;
  }
}
