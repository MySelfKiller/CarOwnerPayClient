package com.kayu.car_owner_pay.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.KWApplication;
import com.kayu.car_owner_pay.R;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.utils.Constants;
import com.kayu.utils.ImageUtil;
import com.kayu.utils.NoMoreClickListener;
import com.kayu.utils.StringUtil;
import com.kayu.utils.callback.Callback;
import com.kayu.utils.callback.ImageCallback;
import com.kayu.utils.permission.EasyPermissions;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;

import java.util.List;

public class CustomerActivity extends BaseActivity {


    private TextView save_btn;
    private MainViewModel mainViewModel;
    private ImageView qrcode_iv;
    private Button call_btn;
    private TextView compay_tv2,compay_tv1;
    Bitmap qrcodeBitmap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        mainViewModel = ViewModelProviders.of(CustomerActivity.this).get(MainViewModel.class);
        //标题栏
//        LinearLayout title_lay = findViewById(R.id.title_lay);
//        title_lay.setBackgroundColor(getResources().getColor(R.color.background_gray));
        TextView title_name = findViewById(R.id.title_name_tv);
        title_name.setText("客服");

        findViewById(R.id.title_back_btu).setOnClickListener(new NoMoreClickListener() {
            @Override
            protected void OnMoreClick(View view) {
                onBackPressed();
            }

            @Override
            protected void OnMoreErrorClick() {

            }
        });
        TextView back_tv = findViewById(R.id.title_back_tv);
        back_tv.setText("我的");
        save_btn = findViewById(R.id.title_arrow_tv);

        qrcode_iv = findViewById(R.id.customer_qrcode_iv);
        compay_tv1 = findViewById(R.id.customer_compay_tv1);
        compay_tv2 = findViewById(R.id.customer_compay_tv2);
        call_btn = findViewById(R.id.customer_call_btn);

        mainViewModel.getCustomer(CustomerActivity.this).observe(CustomerActivity.this, new Observer<SystemParam>() {
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
                            permissionsCheck(new String[]{Manifest.permission.CALL_PHONE}, R.string.permiss_call_phone,new Callback() {
                                @Override
                                public void onSuccess() {
                                    KWApplication.getInstance().callPhone(CustomerActivity.this,systemParam.content);
                                }

                                @Override
                                public void onError() {

                                }
                            });

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
                        permissionsCheck(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.string.permiss_write_store,new Callback() {
                            @Override
                            public void onSuccess() {
                                if (null == qrcodeBitmap) {
                                    ToastUtils.show("保存图片不存在");
                                    return;
                                }
                                String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
                                boolean isSaveSuccess = ImageUtil.saveImageToGallery(CustomerActivity.this, qrcodeBitmap,fileName);
                                if (isSaveSuccess) {
                                    ToastUtils.show("保存成功");
                                } else {
                                    ToastUtils.show("保存失败");
                                }
                            }

                            @Override
                            public void onError() {

                            }
                        });



                    }

                    @Override
                    protected void OnMoreErrorClick() {

                    }
                });
            }
        });
    }

    public void permissionsCheck(String[] perms, int resId, @NonNull Callback callback) {
//        String[] perms = {Manifest.permission.CAMERA};
        performCodeWithPermission(1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, perms, new PermissionCallback() {
            @Override
            public void hasPermission(List<String> allPerms) {
                callback.onSuccess();
            }

            @Override
            public void noPermission(List<String> deniedPerms, List<String> grantedPerms, Boolean hasPermanentlyDenied) {
                EasyPermissions.goSettingsPermissions(CustomerActivity.this, 1, Constants.RC_PERMISSION_PERMISSION_FRAGMENT, Constants.RC_PERMISSION_BASE);
            }

            @Override
            public void showDialog(int dialogType, final EasyPermissions.DialogCallback callback) {
                MessageDialog dialog = MessageDialog.build((AppCompatActivity) CustomerActivity.this);
                dialog.setTitle("需要获取以下权限");
                dialog.setMessage(getString(resId));
                dialog.setOkButton("下一步", new OnDialogButtonClickListener() {

                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        callback.onGranted();
                        return false;
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }
}