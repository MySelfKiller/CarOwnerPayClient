package com.kayu.form_verify.validator;

import android.content.Context;
import android.util.Patterns;

import com.kayu.form_verify.AbstractValidator;
import com.kayu.form_verify.R;

import java.util.regex.Pattern;

public class UrlValidator extends AbstractValidator {

    private static Pattern mPattern = Patterns.WEB_URL;

	private int mErrorMessage = R.string.validator_url;

	public UrlValidator(Context c) {
		super(c);
	}

    public UrlValidator(Context c, int errorMessage) {
        super(c);
        mErrorMessage = errorMessage;
    }

	@Override
	public boolean isValid(String url) {
		return mPattern.matcher(url).matches();
	}

	@Override
	public String getMessage() {
		return mContext.getString(mErrorMessage);
	}

}
