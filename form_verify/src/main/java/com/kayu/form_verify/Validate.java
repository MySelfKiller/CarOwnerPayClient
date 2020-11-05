package com.kayu.form_verify;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;


public class Validate extends AbstractValidate{

	/**
     * Validator chain
     */
    protected ArrayList<AbstractValidator> _validators = new ArrayList<AbstractValidator>();
    
    /**
     * Validation failure messages
     */
    protected String _message = "";
    
    /**
     * 
     */
    protected TextView _source;
    
    
    public Validate(TextView source){
    	this._source = source;
    }

    /**
     * Adds a validator to the start of the chain
     *
     * @param validator
     */
    public void addValidator(AbstractValidator validator)
    {
    	this._validators.add(validator);
    	return;
    }

    @Override
    public boolean isValid(String value) {
        if (null == value || value.length()<=0){
            this._message = "输入不能为空";
            return false;
        }
        boolean result = true;
        Iterator<AbstractValidator> it = this._validators.iterator();
        if (this._validators.isEmpty()){
            if (this._source.getText().toString().equals(value)){
                return true;
            }
            else{
                this._source.setError(this._message);
                return false;
            }

        }
        while(it.hasNext()){
            AbstractValidator validator = it.next();
            try{
                if(!validator.isValid(value)){
                    this._message = validator.getMessage();
                    result = false;
                    break;
                }
            }catch(ValidatorException e){
                System.err.println(e.getMessage());
                System.err.println(e.getStackTrace());
                this._message = e.getMessage();
                result = false;
                break;
            }
        }

        return result;
    }

    public boolean isValid(String value,String errorMessage){
    	this._message = errorMessage;

    	return isValid(value);
    }
    
    public String getMessages(){
    	return this._message;
    }
    
    public TextView getSource(){
    	return this._source;
    }
    
}
