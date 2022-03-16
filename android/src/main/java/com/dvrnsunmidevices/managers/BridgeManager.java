package com.dvrnsunmidevices.managers;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.nfc.Tag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dvrnsunmidevices.utils.SunmiUtil;
import com.dvrnsunmidevices.utils.tasks.BitmapGeneratingAsyncTask;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;

import java.io.IOException;

public class BridgeManager extends ReactContextBaseJavaModule implements BitmapGeneratingAsyncTask.BitmapGenerationCallback {
  private final ReactApplicationContext reactContext;
  private Promise promise;

  public BridgeManager(@Nullable ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    SunmiUtil.getInstance().connectPrinterService(reactContext);
  }

  public void printCustomHTMl(String html, Promise promise) {
    this.promise = promise;
    SunmiUtil.getInstance().passCallback(promise);

    int orientation = reactContext.getResources().getConfiguration().orientation;
    BitmapGeneratingAsyncTask generator;
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      generator = new BitmapGeneratingAsyncTask(reactContext, html, 380, this, 150);
    } else {
      generator = new BitmapGeneratingAsyncTask(reactContext, html, 580, this, 150);
    }
    generator.execute();
  }

  @Override
  public void done(Bitmap bitmap, Exception error) {
    try {
      HardwareManager.getInstance().printBitmap(bitmap);
    } catch (java.lang.Exception e) {
      promise.reject(error);
      SunmiUtil.getInstance().disconnectPrinterService(reactContext);
      e.printStackTrace();
    }

  }

  @NonNull
  @Override
  public String getName() {

    return "BridgeManager";
  }


  public void showTwoLineText(String firstRow, String secondRow, Promise promise) {
    SunmiUtil.getInstance().passCallback(promise);
    HardwareManager.getInstance().showTwoLineText(firstRow, secondRow);
  }

  public void writeNFCTag(ReadableMap data, Tag tag, Promise promise) {
    try {
      System.out.println(data);
      HardwareManager.getInstance().passCallback(promise);
      HardwareManager.getInstance().writeToNFCTag(tag,
        HardwareManager.getInstance().generateTextToWriteNFC(
          data.getString("user"),
          data.getString("password"),
          data.getString("domain"))
      );
    } catch (NullPointerException | IOException e) {
      promise.reject(e);
      e.printStackTrace();
    }

  }
}
