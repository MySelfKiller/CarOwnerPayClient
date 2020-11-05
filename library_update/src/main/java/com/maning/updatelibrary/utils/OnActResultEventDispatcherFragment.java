package com.maning.updatelibrary.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;

/**
 * author : maning
 * time   : 2018/06/04
 * desc   :
 * version: 1.0
 */
public class OnActResultEventDispatcherFragment extends Fragment {
    public static final String TAG = "on_act_result_event_dispatcher";

    private ActForResultCallback mCallback = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startForResult(Intent intent, ActForResultCallback callback) {
        mCallback = callback;
        startActivityForResult(intent, 111);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCallback != null) {
            mCallback.onActivityResult(resultCode, data);
        }
    }
}
