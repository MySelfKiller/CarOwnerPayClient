package com.kayu.car_owner_pay.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.activity.MainViewModel;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.utils.ImageUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.ImageCallback;

public class CustomerFragment extends Fragment {


    private TextView save_btn;
    private MainViewModel mainViewModel;
    private ImageView qrcode_iv;
    private Button call_btn;
    private TextView compay_tv2,compay_tv1;
    Bitmap qrcodeBitmap = null;

    public CustomerFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);
        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = view.findViewById(R.id.title_name_tv);
        title_name.setText("客服");

        view.findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                requireActivity().onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView back_tv = view.findViewById(R.id.title_back_tv);
        back_tv.setText("我的");
        save_btn = view.findViewById(R.id.title_arrow_tv);

        qrcode_iv = view.findViewById(R.id.customer_qrcode_iv);
        compay_tv1 = view.findViewById(R.id.customer_compay_tv1);
        compay_tv2 = view.findViewById(R.id.customer_compay_tv2);
        call_btn = view.findViewById(R.id.customer_call_btn);

        mainViewModel.getCustomer(getContext()).observe(requireActivity(), new Observer<SystemParam>() {
            @Override
            public void onChanged(SystemParam systemParam) {
                if (null == systemParam)
                    return;

                compay_tv1.setText(StringUtil.isEmpty(systemParam.blank9)? "扫码添加客服微信": systemParam.blank9);
                if (StringUtil.isEmpty(systemParam.content)) {
                    compay_tv2.setVisibility(View.GONE);
                    call_btn.setVisibility(View.GONE);
                } else {
                    call_btn.setVisibility(View.VISIBLE);
                    call_btn.setOnClickListener(new NoMoreClickListener() {
                        @Override
                        protected void OnMoreClick(View view) {
                            KWApplication.getInstance().callPhone(requireActivity(),systemParam.content);
                        }

                        @Override
                        protected void OnMoreErrorClick() {

                        }
                    });
                    compay_tv2.setVisibility(View.VISIBLE);
                    compay_tv2.setText("客服电话："+ systemParam.content);

                }
                KWApplication.getInstance().loadImg(systemParam.url,qrcode_iv,new ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap resource) {
                        qrcodeBitmap = resource;
                    }

                    @Override
                    public void onError() {

                    }
                });
                save_btn.setVisibility(View.VISIBLE);
                save_btn.setOnClickListener(new NoMoreClickListener() {
                    @Override
                    protected void OnMoreClick(View view) {
                        if (null == qrcodeBitmap) {
                            Toast.makeText(getContext(),"保存图片不存在",Toast.LENGTH_LONG).show();
                            return;
                        }
                        String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
                        boolean isSaveSuccess = ImageUtil.saveImageToGallery(getActivity(), qrcodeBitmap,fileName);
                        if (isSaveSuccess) {
                            Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });
            }
        });

    }
}