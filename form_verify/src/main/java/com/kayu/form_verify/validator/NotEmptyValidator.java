package com.kayu.form_verify.validator;

import android.content.Context;

import com.kayu.form_verify.AbstractValidator;
import com.kayu.form_verify.R;


public class NotEmptyValidator extends AbstractValidator {

	private int mErrorMessage = R.string.validator_empty;


	public NotEmptyValidator(Context c) {
		super(c);
	}

    public NotEmptyValidator(Context c, int errorMessage) {
        super(c);
        mErrorMessage = errorMessage;
    }

	@Override
	public boolean isValid(String value) {
		if(value != null){
			if(value.length() > 0)
				return true;
			else
				return false;
		}else{
			return false;
		}
	}

	@Override
	public String getMessage() {
		return mContext.getString(mErrorMessage);
	}

}
