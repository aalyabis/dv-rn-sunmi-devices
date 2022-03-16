/*
 * Copyright (c) 2021. DEEP VISION s.r.o.
 * Author: Lukáš Outlý
 * Project: Speedlo POS
 */

package com.dvrnsunmidevices.callback;

public interface PrinterCallback {
    String getResult();

    void onReturnString(String result);
}
