package com.kayu.car_owner_pay.ui.text_link;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import com.kayu.car_owner_pay.activity.WebViewActivity;

public class UrlClickableSpan extends ClickableSpan {
    private Context context;
    private String url;
    public UrlClickableSpan(Context context, String url){
        this.context = context;
        this.url = url;
    }

    @Override
    public void onClick(@NonNull View widget) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);

    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
//        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
