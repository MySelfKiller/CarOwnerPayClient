package com.kayu.form_verify.validator;

import android.content.Context;

import com.kayu.form_verify.AbstractValidator;
import com.kayu.form_verify.R;

import java.util.regex.Pattern;

/**
 *
 */
public class BankCardValidator extends AbstractValidator {


    /**
     *
     */
    private static final Pattern mPattern = Pattern.compile("^[0-9]{10,}$");

    private int mErrorMessage = R.string.validator_bank_card_str;

    public BankCardValidator(Context c) {
        super(c);
    }

    public BankCardValidator(Context c, int errorMessage) {
        super(c);
        mErrorMessage = errorMessage;
    }

    @Override
    public boolean isValid(String value) {
        return mPattern.matcher(value).matches();
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}
