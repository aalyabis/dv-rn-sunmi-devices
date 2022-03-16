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

import com.dvrnsunmidevices.utils.SunmiUtil;
import com.facebook.react.bridge.Promise;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    // TODO :update
    for (String pageData : data) {
      ultralight.writePage(pageNumber, pageData.getBytes(Charset.forName("US-ASCII")));
      pageNumber++;
    }
    ultralight.close();
    promise.resolve("NFC chip data write completed");
  }

  public List<String> generateTextToWriteNFC(String user, String password, String domain) {
    int startIndex = 0;
    int endIndex = 4;

    String textToWrite = user + ";" + password + ";" + domain + ";";

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

  public String[] readTagData(Tag tagFromIntent) throws IOException {
    MifareUltralight uTag = MifareUltralight.get(tagFromIntent);

    String user = "";
    String pwd = "";
    String domain = "";

    uTag.connect();

    byte[] data = uTag.readPages(4);
    byte[] data2 = uTag.readPages(8);
    byte[] data3 = uTag.readPages(12);
    String text = new String(data, "UTF-8");
    String text2 = new String(data2, "UTF-8");
    String text3 = new String(data3, "UTF-8");

    String finText = text + text2 + text3;
    List<String> result = Arrays.asList(finText.split(";"));

    int lineNum = 0;
    for (String line : result) {
      switch (lineNum) {
        case 0:
          user=line;
        case 1:
          pwd = line;
          break;
        case 2:
          domain = line;
          break;
        default:
          break;
      }

      lineNum++;
    }
    uTag.close();
    return new String[]{user, pwd,domain};
  }
}
