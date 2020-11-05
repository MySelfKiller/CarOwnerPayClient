package com.kayu.car_owner_pay.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kayu.car_owner_pay.R;


public class WashUnpaidFragment extends Fragment {
    private String shopCode;

    public WashUnpaidFragment(String shopCode) {
        this.shopCode = shopCode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wash_unpaid, container, false);
    }
}