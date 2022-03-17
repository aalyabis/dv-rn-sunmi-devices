/*
 * Copyright (c) 2021. DEEP VISION s.r.o.
 * Author: Lukáš Outlý
 * Project: Speedlo POS
 */


package com.dvrnsunmidevices.managers;

import android.graphics.Bitmap;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;

import com.dvrnsunmidevices.BaseApp;
import com.dvrnsunmidevices.utils.SunmiUtil;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HardwareManager {
  private static HardwareManager mHardwareManager;
  private Promise promise;


  private HardwareManager() {
  }

  public static HardwareManager getInstance() {
    if (mHardwareManager == null)
      mHardwareManager = new HardwareManager();
    return mHardwareManager;
  }

  public void passCallback(Promise promise) {
    this.promise = promise;
  }

  public void initPrinter() {
    SunmiUtil.getInstance().initPrinter();
  }

  public void openDrawer() {
    SunmiUtil.getInstance().openDrawer();
  }

  public void showTwoLineText(String lineOne, String lineTwo) {
    SunmiUtil.getInstance().show2LineText(lineOne, lineTwo);
  }

/*  public void printImage(String imgUrl) {
    Bitmap logo = BitmapUtil.getBitmapFromURL(imgUrl);
    SunmiUtil.getInstance().printBitmap(BitmapUtil.scaleImage(logo, 150, true));
  }*/
/*
  public void showBitmap(Bitmap image) {
    SunmiUtil.getInstance().showBitmap(image);
  }*/

  public void printBitmap(Bitmap image) {
    SunmiUtil.getInstance().initPrinter();
    SunmiUtil.getInstance().printBitmapSplit(image);
    SunmiUtil.getInstance().cutPaper(null);
  }

  public void print3EmptyRows() {
    SunmiUtil.getInstance().print3Line();
  }

  public void cutPaper() {
    SunmiUtil.getInstance().cutPaper(null);
  }

  public void writeToNFCTag(Tag tag, List<String> data) throws IOException {
    MifareUltralight ultralight = null;
    ultralight = MifareUltralight.get(tag);
    if (ultralight == null) return;
    ultralight.connect();
    int pageNumber = 4;
    for (String pageData : data) {
      ultralight.writePage(pageNumber, pageData.getBytes(Charset.forName("US-ASCII")));
      pageNumber++;
    }
    ultralight.close();
    promise.resolve("NFC chip data write completed");
  }

  public List<String> generateTextToWriteNFC(ReadableMap map) {
    int startIndex = 0;
    int endIndex = 4;

    String textToWrite = "";

    Iterator<Map.Entry<String, Object>> entryIterator = map.getEntryIterator();
    while (entryIterator.hasNext()) {
      Map.Entry<String, Object> nextEl = entryIterator.next();
      textToWrite += nextEl.getKey() + ";" + nextEl.getValue() + ";";
    }

    int textLen = textToWrite.length();
    int parts = textLen / 4;
    if (textLen % 4 != 0) {
      parts++;
    }

    List<String> partsToWrite = new ArrayList<>();

    for (int i = 0; i < parts; i++) {
      boolean isLast = false;
      if (endIndex >= textLen) {
        isLast = true;
        endIndex = textLen;
      }

      StringBuilder part = new StringBuilder(textToWrite.substring(startIndex, endIndex));
      startIndex = endIndex;
      endIndex += 4;

      if (isLast) {
        int charsToAdd = 4 - part.length();
        for (int y = 0; y < charsToAdd; y++) {
          part.append("0");
        }
      }
      partsToWrite.add(part.toString());
    }

    return partsToWrite;
  }

  public WritableMap readTagData(Tag tagFromIntent) throws IOException {
    MifareUltralight uTag = MifareUltralight.get(tagFromIntent);

    uTag.connect();

    String finText = "";
    for (int i = 1; i < 20; i++) {
      byte[] data = uTag.readPages(4*i);
      String text = new String(data, "UTF-8");
      finText += text;

    }
    List<String> result = Arrays.asList(finText.split(";"));

    int lineNum = 0;
    WritableMap writableMap = Arguments.createMap();

    String key = "";
    for (String line : result) {
      System.out.println("One line of result: " + line);
      if (lineNum % 2 == 0) {
        key = line;
      } else {
        writableMap.putString(key, line);
      }
      lineNum++;
    }
    uTag.close();
    return writableMap;
  }
}
