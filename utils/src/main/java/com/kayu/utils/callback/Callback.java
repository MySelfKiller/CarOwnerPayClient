package com.kayu.utils.callback;

public interface Callback {
    void onSuccess();

    void onError();

    public static class EmptyCallback implements Callback {

        @Override public void onSuccess() {
        }

        @Override public void onError() {
        }
    }
}
