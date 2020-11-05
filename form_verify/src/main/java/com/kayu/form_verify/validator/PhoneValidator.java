package com.kayu.form_verify.validator;

import android.content.Context;
import android.util.Patterns;

import com.kayu.form_verify.AbstractValidator;
import com.kayu.form_verify.R;
import com.kayu.form_verify.ValidatorException;

import java.util.regex.Pattern;

/**
 * Validator to check if Phone number is correct.
 * Created by throrin19 on 13/06/13.
 */
public class PhoneValidator extends AbstractValidator {

    private static final Pattern mPattern = Patterns.PHONE;

    private int mErrorMessage = R.string.validator_phone;

    public PhoneValidator(Context c) {
        super(c);
    }

    public PhoneValidator(Context c, int errorMessage) {
        super(c);
        mErrorMessage = errorMessage;
    }

    @Override
    public boolean isValid(String value) throws ValidatorException {
        Pattern pattern=Pattern.compile("^1[0-9]{10}$");
        return pattern.matcher(value).matches();
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}
