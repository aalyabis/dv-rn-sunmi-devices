/*
 * Copyright (c) 2021. DEEP VISION s.r.o.
 * Author: Lukáš Outlý
 * Project: Speedlo POS
 */

package com.dvrnsunmidevices.utils.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.Html2BitmapConfigurator;
import com.izettle.html2bitmap.content.WebViewContent;

import java.lang.ref.WeakReference;


public class BitmapGeneratingAsyncTask extends AsyncTask<Void, Void, Bitmap> {

  private final WeakReference<Context> context;
  private final String html;
  private final int width;
  private final int zoom;
  private final WeakReference<BitmapGenerationCallback> callback;
  private Exception error = null;

  public BitmapGeneratingAsyncTask(Context context, String html, int width, BitmapGenerationCallback callback, int zoom) {
    this.context = new WeakReference<>(context.getApplicationContext());
    this.html = html;
    this.width = width;
    this.zoom = zoom;
    this.callback = new WeakReference<>(callback);
  }

  @Override
  protected Bitmap doInBackground(Void... voids) {
    Context context = this.context.get();

    try {
      Html2BitmapConfigurator html2BitmapConfigurator = new Html2BitmapConfigurator() {
        @Override
        public void configureWebView(WebView webview) {
          webview.setBackgroundColor(Color.WHITE);
          webview.getSettings().setTextZoom(zoom);
        }
      };

      Html2Bitmap build = new Html2Bitmap.Builder()
        .setContext(context)
        .setContent(WebViewContent.html(html))
        .setBitmapWidth(width)
        .setMeasureDelay(10)
        .setScreenshotDelay(10)
        .setStrictMode(true)
        .setTimeout(10)
        .setTextZoom(zoom)
        .setConfigurator(html2BitmapConfigurator)
        .build();

      return build.getBitmap();
    } catch (Exception e) {
      error = e;
      return null;
    }
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    BitmapGenerationCallback bitmapGenerationCallback = this.callback.get();
    if (bitmapGenerationCallback != null) {
      bitmapGenerationCallback.done(bitmap,error);
    }
  }

  public interface BitmapGenerationCallback {
    void done(Bitmap bitmap,Exception e);
  }
}
