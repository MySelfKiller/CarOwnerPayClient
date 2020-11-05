package com.kayu.car_owner_pay.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kayu.car_owner_pay.R;
import com.kayu.utils.status_bar_set.StatusBarUtil;

public class ConsultFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
        View root = inflater.inflate(R.layout.fragment_consult, container, false);
        return root;
    }
}