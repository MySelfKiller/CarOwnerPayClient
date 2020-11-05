package com.flyco.tablayout.listener;

import androidx.annotation.DrawableRes;

public interface CustomTabEntity {
    String getTabTitle();
    String getTabTitle2();

    @DrawableRes
    int getTabSelectedIcon();

    @DrawableRes
    int getTabUnselectedIcon();
}