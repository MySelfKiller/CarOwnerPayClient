package com.kayu.form_verify.validator;

import android.content.Context;

import com.kayu.form_verify.AbstractValidator;
import com.kayu.form_verify.R;
import com.kayu.form_verify.ValidatorException;

import java.util.regex.Pattern;

/**
 *
 */
public class NumberValidator extends AbstractValidator {

    private static final Pattern mPattern = Pattern.compile("^[0-9]*$");

    private int mErrorMessage = R.string.validator_number;

    public NumberValidator(Context c) {
        super(c);
    }

    public NumberValidator(Context c, int errorMessage) {
        super(c);
        mErrorMessage = errorMessage;
    }

    @Override
    public boolean isValid(String value) throws ValidatorException {
        return mPattern.matcher(value).matches();
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}
