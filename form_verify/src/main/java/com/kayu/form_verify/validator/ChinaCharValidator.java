package com.kayu.form_verify.validator;

import android.content.Context;

import com.kayu.form_verify.AbstractValidator;
import com.kayu.form_verify.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ChinaCharValidator extends AbstractValidator {

    /**
     *
     */
    private static final Pattern mPattern = Pattern.compile("[\u4e00-\u9fa5]");

    private int mErrorMessage = R.string.validator_china_str;

    public ChinaCharValidator(Context c) {
        super(c);
    }

    public ChinaCharValidator(Context c, int errorMessage) {
        super(c);
        mErrorMessage = errorMessage;
    }

    @Override
    public boolean isValid(String value) {
        if (null == value || value.length()<=0)
            return false;
        char c[] = value.toCharArray();
        for(int i=0;i<c.length;i++){
            Matcher matcher = mPattern.matcher(String.valueOf(c[i]));
            if(!matcher.matches()){
                return false;
            }
        }
        return true;
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}
