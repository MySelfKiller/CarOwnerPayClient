package com.kayu.car_owner_pay.http;

import android.os.Handler;

import com.kayu.utils.Constants;
import com.kayu.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by xubin on 2018/3/14.
 */

public class FileCallBack implements Callback {
    private RequestInfo reqInfo = null;
    private Handler handler = null;
    private long downfileSize = 0;
    private File downloadFile = null;
    private long apkSize = 0;
    private boolean isStop = false;

    public FileCallBack(RequestInfo requestInfo) {
        reqInfo = requestInfo;
        handler = reqInfo.handler;
        downloadFile = reqInfo.file;
        apkSize = reqInfo.fileSize;
        if (reqInfo.file != null) {
            downfileSize = reqInfo.file.length();
        }
    }

    public boolean getStop() {
        return isStop;
    }

    public void Stop() {
        isStop = true;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Handler handler = reqInfo.handler;
        ResponseInfo responseInfo = new ResponseInfo(-1, "网络异常");
        handler.sendMessage(handler.obtainMessage(Constants.REQ_NETWORK_ERROR, responseInfo));
        LogUtil.e("network req", "IOException: " + e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        LogUtil.e("network req", "IOException: " +"response.code()="+ response.code()+"\n"+response.toString());
        if (response.code() > 199 && response.code() <= 499) {
            byte[] buff = new byte[1024];
            int len;
            long total = downfileSize;
            InputStream in = response.body().byteStream();
            FileOutputStream fos = null;
            if (downfileSize > 0)
                fos = new FileOutputStream(downloadFile, true);
            else
                fos = new FileOutputStream(downloadFile);

            handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_REFRESH, (int) downfileSize, (int) apkSize));
            while ((len = in.read(buff)) != -1) {
                if (isStop) {
                    handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_END, (int) total, (int) apkSize));
                    return;
                }
                fos.write(buff, 0, len);
                total += len;
                final long finalTotal = total;
                handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_REFRESH, (int) finalTotal, (int) apkSize));

            }
            fos.flush();
            fos.close();
            in.close();
            handler.sendMessage(handler.obtainMessage(Constants.PARSE_DATA_SUCCESS));
        } else {
            ResponseInfo obj = new ResponseInfo(-1, "网络异常");
            handler.sendMessage(handler.obtainMessage(Constants.REQ_NETWORK_ERROR, obj));
        }

    }
}
