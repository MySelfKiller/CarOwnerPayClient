package com.kayu.form_verify.validator;

import android.content.Context;

import com.kayu.form_verify.AbstractValidator;
import com.kayu.form_verify.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 */
public class IdNumberValidator extends AbstractValidator {

    /**
     *
     */
    private static final Pattern mPattern =
            Pattern.compile("^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X|x)?$"); //粗略的校验

    private int mErrorMessage = R.string.validator_id_number;

    public IdNumberValidator(Context c) {
        super(c);
    }

    public IdNumberValidator(Context c, int errorMessage) {
        super(c);
        mErrorMessage = errorMessage;
    }

    @Override
    public boolean isValid(String value) {
        Matcher matcher = mPattern.matcher(value);
        if(matcher.matches()){
            return true;
        }
        return false;
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}
