package com.kayu.car_owner_pay.extend;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.alibctriver.AlibcNavigateCenter;

public class ShareImpl implements AlibcNavigateCenter.IUrlNavigate{

    @Override
    public boolean openUrl(Context context, String url) {
        // TODO 具体的分享实现
        Toast.makeText(context, "分享链接: " + url, Toast.LENGTH_LONG).show();
        return true;
    }
}
