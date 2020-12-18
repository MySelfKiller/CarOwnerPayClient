package com.kongzue.dialog.v3;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kongzue.dialog.R;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.interfaces.OnShowListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.util.view.BlurView;
import com.kongzue.dialog.util.view.ProgressView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.kongzue.dialog.util.DialogSettings.blurAlpha;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/3/22 16:16
 */
public class TipGifDialog extends BaseDialog {

    private static AppCompatActivity mContext;
    private DialogSettings.THEME tipTheme;

    public enum TYPE {
        WARNING, SUCCESS, ERROR, OTHER
    }

    private OnDismissListener dismissListener;

    public static TipGifDialog waitDialogTemp;
    protected CharSequence message;
    private TYPE type;
    private GifDrawable tipImage;

    private BlurView blurView;

    private RelativeLayout boxBody;
    private RelativeLayout boxBlur;
    private RelativeLayout boxProgress;
    private RelativeLayout boxProgressGif;
    private GifImageView progressGif;
    private ProgressView progress;
//    private RelativeLayout boxTip;
    private TextView txtInfo;

    private int tipTime = 1500;

    protected TipGifDialog() {
    }
    
    public static TipGifDialog build(AppCompatActivity context) {
        synchronized (TipGifDialog.class) {
            TipGifDialog waitDialog = new TipGifDialog();
            
            if (waitDialogTemp == null) {
                waitDialogTemp = waitDialog;
            } else {
                if (waitDialogTemp.context.get() != context) {
                    dismiss();
                    waitDialogTemp = waitDialog;
                } else {
                    waitDialog = waitDialogTemp;
                }
            }
            waitDialog.log("装载提示/等待框: " + waitDialog.toString());
            waitDialog.context = new WeakReference<>(context);
            waitDialog.build(waitDialog, R.layout.dialog_wait);
            return waitDialog;
        }
    }
    
    public static TipGifDialog showWait(AppCompatActivity context, CharSequence message) {
        synchronized (TipGifDialog.class) {
            TipGifDialog waitDialog = build(context);
            
            waitDialogTemp.onDismissListener = new OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (waitDialogTemp != null && waitDialogTemp.dismissListener != null)
                        waitDialogTemp.dismissListener.onDismiss();
                    waitDialogTemp = null;
                }
            };
            
            if (waitDialog == null) {
                waitDialogTemp.setTip(null);
                waitDialogTemp.setMessage(message);
                if (waitDialogTemp.cancelTimer != null) waitDialogTemp.cancelTimer.cancel();
                return waitDialogTemp;
            } else {
                waitDialog.message = message;
                waitDialog.type = null;
                waitDialog.tipImage = null;
                if (waitDialog.cancelTimer != null) waitDialog.cancelTimer.cancel();
                waitDialog.showDialog();
                return waitDialog;
            }
        }
    }
    
    public static TipGifDialog showWait(AppCompatActivity context, int messageResId) {
        synchronized (TipGifDialog.class) {
            TipGifDialog waitDialog = build(context);
            
            waitDialogTemp.onDismissListener = new OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (waitDialogTemp != null && waitDialogTemp.dismissListener != null)
                        waitDialogTemp.dismissListener.onDismiss();
                    waitDialogTemp = null;
                }
            };
            
            if (waitDialog == null) {
                waitDialogTemp.setTip(null);
                waitDialogTemp.setMessage(context.getString(messageResId));
                if (waitDialogTemp.cancelTimer != null) waitDialogTemp.cancelTimer.cancel();
                return waitDialogTemp;
            } else {
                waitDialog.message = context.getString(messageResId);
                waitDialog.type = null;
                waitDialog.tipImage = null;
                if (waitDialog.cancelTimer != null) waitDialog.cancelTimer.cancel();
                waitDialog.showDialog();
                return waitDialog;
            }
        }
    }

    public static TipGifDialog show(AppCompatActivity context, CharSequence message, TYPE type) {
        synchronized (TipGifDialog.class) {
            TipGifDialog waitDialog = build(context);
            
            waitDialogTemp.onDismissListener = new OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (waitDialogTemp != null && waitDialogTemp.dismissListener != null)
                        waitDialogTemp.dismissListener.onDismiss();
                    waitDialogTemp = null;
                }
            };
            
            if (waitDialog == null) {
                waitDialogTemp.setTip(type);
                waitDialogTemp.setMessage(message);
                waitDialogTemp.autoDismiss();
                return waitDialogTemp;
            } else {
                waitDialog.message = message;
                waitDialog.setTip(type);
                waitDialog.showDialog();
                waitDialog.autoDismiss();
                return waitDialog;
            }
        }
    }

    public static TipGifDialog show(AppCompatActivity context, CharSequence message, TYPE type, int resID) {
        synchronized (TipGifDialog.class) {
            TipGifDialog waitDialog = build(context);
            mContext = context;
            waitDialogTemp.onDismissListener = new OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (waitDialogTemp != null && waitDialogTemp.dismissListener != null)
                        waitDialogTemp.dismissListener.onDismiss();
                    waitDialogTemp = null;
                }
            };

            if (waitDialog == null) {
//                waitDialogTemp.setTip(type);
                waitDialogTemp.setTip(type,context.getResources(),resID);
                waitDialogTemp.setMessage(message);
                waitDialogTemp.autoDismiss();
                return waitDialogTemp;
            } else {
                waitDialog.message = message;
//                waitDialog.setTip(type);
                waitDialog.setTip(type,context.getResources(),resID);
                waitDialog.showDialog();
                waitDialog.autoDismiss();
                return waitDialog;
            }
        }
    }

    public static TipGifDialog show(AppCompatActivity context, int messageResId, TYPE type) {
        return show(context, context.getString(messageResId), type);
    }
    

    protected void showDialog() {
        log("启动提示/等待框 -> " + toString());
        super.showDialog();
        setDismissEvent();
    }
    
    private View rootView;
    
    @Override
    public void bindView(View rootView) {
        if (boxBlur != null) {
            boxBlur.removeAllViews();
        }
        this.rootView = rootView;
        boxBody = rootView.findViewById(R.id.box_body);
        boxBlur = rootView.findViewById(R.id.box_blur);
        boxProgress = rootView.findViewById(R.id.box_progress);
        boxProgressGif = rootView.findViewById(R.id.box_progress_gif);
        progressGif = rootView.findViewById(R.id.progress_gif);
        progress = rootView.findViewById(R.id.progress);
        txtInfo = rootView.findViewById(R.id.txt_info);
        
        refreshView();
        if (onShowListener != null) onShowListener.onShow(this);
    }
    
    private Timer cancelTimer;
    
    private void autoDismiss() {
        if (cancelTimer != null) cancelTimer.cancel();
        cancelTimer = new Timer();
        cancelTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                doDismiss();
                dismiss();
                cancelTimer.cancel();
            }
        }, tipTime);
    }
    
    public void refreshView() {
        if (rootView != null) {
            final int bkgResId, blurFrontColor;
            if (tipTheme == null) tipTheme = DialogSettings.tipTheme;
            if (DialogSettings.tipBackgroundResId != 0 && backgroundResId == -1) {
                backgroundResId = DialogSettings.tipBackgroundResId;
            }
            
            switch (tipTheme) {
                case LIGHT:
                    bkgResId = R.drawable.rect_light;
                    int darkColor = Color.rgb(0, 0, 0);
                    blurFrontColor = Color.argb(blurAlpha, 255, 255, 255);
                    if (progress != null) {
                        progress.setup(R.color.black);
                    }
                    txtInfo.setTextColor(darkColor);
                    if (type != null) {
                        boxProgress.setVisibility(View.GONE);
                        boxProgressGif.setVisibility(View.VISIBLE);
                        switch (type) {
                            case OTHER:
                                progressGif.setImageDrawable(tipImage);
                                break;
                            case ERROR:
                                progressGif.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_error_dark));
                                break;
                            case WARNING:
                                progressGif.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_warning_dark));
                                break;
                            case SUCCESS:
                                progressGif.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_finish_dark));
                                break;
                        }
                    } else {
                        boxProgress.setVisibility(View.VISIBLE);
                        boxProgressGif.setVisibility(View.GONE);
                    }
                    break;
                case DARK:
                    bkgResId = R.drawable.rect_dark;
                    int lightColor = Color.rgb(255, 255, 255);
                    blurFrontColor = Color.argb(blurAlpha, 0, 0, 0);
                    if (progress != null) {
                        progress.setup(R.color.white);
                    }
                    txtInfo.setTextColor(lightColor);
                    if (type != null) {
                        boxProgress.setVisibility(View.GONE);
                        boxProgressGif.setVisibility(View.VISIBLE);
                        switch (type) {
                            case OTHER:
                                progressGif.setImageDrawable(tipImage);
                                break;
                            case ERROR:
                                progressGif.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_error));
                                break;
                            case WARNING:
                                progressGif.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_warning));
                                break;
                            case SUCCESS:
                                progressGif.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.img_finish_dark));
                                break;
                        }
                    } else {
                        boxProgress.setVisibility(View.VISIBLE);
                        boxProgressGif.setVisibility(View.GONE);
                    }
                    break;
                default:
                    bkgResId = R.drawable.rect_dark;
                    blurFrontColor = Color.argb(blurAlpha, 0, 0, 0);
                    break;
            }
            if (backgroundResId != -1) {
                boxBody.setBackgroundResource(backgroundResId);
            } else {
                
                if (DialogSettings.isUseBlur) {
                    boxBlur.post(new Runnable() {
                        @Override
                        public void run() {
                            blurView = new BlurView(context.get(), null);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            blurView.setOverlayColor(blurFrontColor);
                            boxBlur.addView(blurView, 0, params);
                        }
                    });
                    boxBody.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (boxBlur != null && boxBody != null) {
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(boxBody.getWidth(), boxBody.getHeight());
                                boxBlur.setLayoutParams(params);
                            }
                        }
                    });
                } else {
                    boxBody.setBackgroundResource(bkgResId);
                }
            }
            
            if (isNull(message)) {
                txtInfo.setVisibility(View.GONE);
            } else {
                txtInfo.setVisibility(View.VISIBLE);
                txtInfo.setText(message);
                useTextInfo(txtInfo, tipTextInfo);
            }
            
            if (customView != null) {
                boxProgress.setVisibility(View.GONE);
            }
        }
    }
    
    protected void setDismissEvent() {
        onDismissListener = new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (dismissListener != null)
                    dismissListener.onDismiss();
                waitDialogTemp = null;
            }
        };
    }
    
    @Override
    public void show() {
        showDialog();
        autoDismiss();
    }
    
    public void showNoAutoDismiss() {
        showDialog();
    }
    
    public OnDismissListener getOnDismissListener() {
        return dismissListener == null ? new OnDismissListener() {
            @Override
            public void onDismiss() {
            
            }
        } : dismissListener;
    }
    
    public TipGifDialog setOnDismissListener(OnDismissListener onDismissListener) {
        this.dismissListener = onDismissListener;
        setDismissEvent();
        return this;
    }
    
    public OnShowListener getOnShowListener() {
        return onShowListener == null ? new OnShowListener() {
            @Override
            public void onShow(BaseDialog dialog) {
            
            }
        } : onShowListener;
    }
    
    public TipGifDialog setOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
        return this;
    }
    
    public static void dismiss() {
        if (waitDialogTemp != null) waitDialogTemp.doDismiss();
        waitDialogTemp = null;
        List<BaseDialog> temp = new ArrayList<>();
        temp.addAll(dialogList);
        for (BaseDialog dialog : temp) {
            if (dialog instanceof TipGifDialog) {
                dialog.doDismiss();
            }
        }
    }
    
    public static void dismiss(int millisecond) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, millisecond);
    }
    
    public CharSequence getMessage() {
        return message;
    }
    
    public TipGifDialog setMessage(CharSequence message) {
        this.message = message;
        log("启动提示/等待框 -> " + toString());
        if (txtInfo != null) txtInfo.setText(message);
        refreshView();
        return this;
    }
    
    public TipGifDialog setMessage(int messageResId) {
        this.message = context.get().getString(messageResId);
        log("启动提示/等待框 -> " + toString());
        if (txtInfo != null) txtInfo.setText(message);
        refreshView();
        return this;
    }
    
    public TipGifDialog setTip(TYPE type) {
        this.type = type;
        if (type != TYPE.OTHER) tipImage = null;
        refreshView();
        return this;
    }
    
    public TipGifDialog setTip(TYPE type,Resources resources, int resId) {
        this.type = type;
        try {
            tipImage =  new GifDrawable( resources, resId );
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshView();
        return this;
    }

    public TYPE getType() {
        return type;
    }
    
    public Drawable getTipImage() {
        return tipImage;
    }
    
    public TextView getTxtInfo() {
        return txtInfo;
    }
    
    public TipGifDialog setTipTime(int tipTime) {
        this.tipTime = tipTime;
        if (type != null) autoDismiss();
        return this;
    }
    
    public TipGifDialog setTheme(DialogSettings.THEME theme) {
        tipTheme = theme;
        refreshView();
        return this;
    }
    
    public DialogSettings.THEME getTheme() {
        return tipTheme;
    }
    
    public boolean getCancelable() {
        return cancelable == BOOLEAN.TRUE;
    }
    
    public TipGifDialog setCancelable(boolean enable) {
        this.cancelable = enable ? BOOLEAN.TRUE : BOOLEAN.FALSE;
        if (dialog != null) dialog.get().setCancelable(cancelable == BOOLEAN.TRUE);
        return this;
    }
    
    public interface OnBindView {
        void onBind(TipGifDialog dialog, View v);
    }
    
    @Deprecated
    public TextInfo getMessageTextInfo() {
        return messageTextInfo;
    }
    
    @Deprecated
    public TipGifDialog setMessageTextInfo(TextInfo messageTextInfo) {
        this.messageTextInfo = messageTextInfo;
        refreshView();
        return this;
    }
    
    public TextInfo getTipTextInfo() {
        return tipTextInfo;
    }
    
    public TipGifDialog setTipTextInfo(TextInfo tipTextInfo) {
        this.tipTextInfo = tipTextInfo;
        refreshView();
        return this;
    }
    
    public int getBackgroundResId() {
        return backgroundResId;
    }
    
    public TipGifDialog setBackgroundResId(int backgroundResId) {
        this.backgroundResId = backgroundResId;
        refreshView();
        return this;
    }
    
    public TipGifDialog setCustomDialogStyleId(int customDialogStyleId) {
        if (isAlreadyShown) {
            error("必须使用 build(...) 方法创建时，才可以使用 setTheme(...) 来修改对话框主题或风格。");
            return this;
        }
        this.customDialogStyleId = customDialogStyleId;
        return this;
    }
    
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    }
    
    public OnBackClickListener getOnBackClickListener() {
        return onBackClickListener;
    }
    
    public TipGifDialog setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
        return this;
    }
}
