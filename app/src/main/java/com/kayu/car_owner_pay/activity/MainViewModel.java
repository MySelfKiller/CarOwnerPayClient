package com.kayu.car_owner_pay.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hjq.toast.ToastUtils;
import com.kayu.car_owner_pay.data_parser.BannerDataParse;
import com.kayu.car_owner_pay.data_parser.CategoryDataParse;
import com.kayu.car_owner_pay.data_parser.ParamOilDataParser;
import com.kayu.car_owner_pay.data_parser.ParamWashDataParser;
import com.kayu.car_owner_pay.data_parser.ParameterDataParser;
import com.kayu.car_owner_pay.data_parser.RefundInfoDataParser;
import com.kayu.car_owner_pay.data_parser.StationDetailDataParser;
import com.kayu.car_owner_pay.data_parser.StationListDataParser;
import com.kayu.car_owner_pay.data_parser.WashOrderDetailDataParser;
import com.kayu.car_owner_pay.data_parser.WashStationDetailDataParser;
import com.kayu.car_owner_pay.data_parser.WashStationListDataParser;
import com.kayu.car_owner_pay.http.HttpConfig;
import com.kayu.car_owner_pay.http.ReqUtil;
import com.kayu.car_owner_pay.http.RequestInfo;
import com.kayu.car_owner_pay.http.ResponseCallback;
import com.kayu.car_owner_pay.http.ResponseInfo;
import com.kayu.car_owner_pay.http.parser.NormalIntParse;
import com.kayu.car_owner_pay.http.parser.NormalStringListParse;
import com.kayu.car_owner_pay.http.parser.NormalStringParse;
import com.kayu.car_owner_pay.http.parser.UserDataParse;
import com.kayu.car_owner_pay.model.BannerBean;
import com.kayu.car_owner_pay.model.CategoryBean;
import com.kayu.car_owner_pay.model.OilStationBean;
import com.kayu.car_owner_pay.model.ParamOilBean;
import com.kayu.car_owner_pay.model.ParamWashBean;
import com.kayu.car_owner_pay.model.RefundInfo;
import com.kayu.car_owner_pay.model.SystemParam;
import com.kayu.car_owner_pay.model.UserBean;
import com.kayu.car_owner_pay.model.WashOrderDetailBean;
import com.kayu.car_owner_pay.model.WashStationBean;
import com.kayu.car_owner_pay.model.WashStationDetailBean;
import com.kayu.utils.Constants;
import com.kayu.utils.GsonHelper;
import com.kayu.utils.ItemCallback;
import com.kayu.utils.StringUtil;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<BannerBean>> bannerListData;//横幅数据
    private MutableLiveData<List<OilStationBean>> stationListData;//加油站列表数据
    private MutableLiveData<List<WashStationBean>> washStationListData;//洗车站列表数据
    private MutableLiveData<ParamOilBean> paramOilData;//加油站筛选参数
    private MutableLiveData<ParamWashBean> paramWashData;//洗车站筛选参数
    private MutableLiveData<OilStationBean> oilStationData;//加油站详情数据
    private MutableLiveData<String> payUrlData;//加油站详情数据
    private MutableLiveData<UserBean> userLiveData;//用户信息


    public MainViewModel() {
    }

    public LiveData<UserBean> getUserInfo(Context context) {
        userLiveData = new MutableLiveData<>();
        loadUserData(context);
        return userLiveData;
    }

    @SuppressLint("HandlerLeak")
    private void loadUserData(Context context) {
        RequestInfo reques = new RequestInfo();
        reques.context = context;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_USER_INFO;
        reques.parser = new UserDataParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                UserBean userBean = null;
                if (resInfo.status ==1 ){
                    userBean = (UserBean) resInfo.responseData;
                    if (null != userBean){
                        SharedPreferences sp = context.getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(Constants.isLogin,true);
                        editor.putString(Constants.userInfo, GsonHelper.toJsonString(userBean));
                        editor.apply();
                        editor.commit();
                    }
                }else {
                    ToastUtils.show(resInfo.msg);
                }
                userLiveData.setValue(userBean);
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestGetJSON(callback);
    }


    public LiveData<String> getPayUrl(Context context, String id, int gunNo) {
//        if (null == payUrlData)
        payUrlData = new MutableLiveData<>();
        loadPayInfo(context, id, gunNo);
        return payUrlData;
    }

    @SuppressLint("HandlerLeak")
    private void loadPayInfo(Context context, String id, int gunNo) {
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_PAY;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("gasId",id);
        dataMap.put("gunNo",gunNo);
        request.reqDataMap = dataMap;
        request.parser = new NormalStringParse();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                String payUrl = null;
                if (response.status == 1) {
                    payUrl = (String) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                payUrlData.setValue(payUrl);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestPostJSON(new ResponseCallback(request));
    }

    private MutableLiveData<String> reminderLiveData;

    public LiveData<String> getReminder(Context context, String city) {
//        if (null == parameterLiveData)
        reminderLiveData = new MutableLiveData<>();
        loadReminder(context, city);
        return reminderLiveData;
    }

    @SuppressLint("HandlerLeak")
    private void loadReminder(Context context,  String city) {
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_ACCOUNT_REMINDER;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("content",city);
        request.reqDataMap = dataMap;
        request.parser = new NormalStringParse();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                String parameter = null;
                if (response.status == 1) {
                    parameter = (String) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                reminderLiveData.setValue(parameter);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestPostJSON(new ResponseCallback(request));
    }

    private MutableLiveData<SystemParam> parameterLiveData;
    private MutableLiveData<SystemParam> userTipLiveData;
    private MutableLiveData<SystemParam> regDialogTipLiveData;
    private MutableLiveData<SystemParam> activityHomeLiveData;
    private MutableLiveData<SystemParam> activitySettingLiveData;

    public LiveData<SystemParam> getHomeActivity(Context context) {
//        if (null == parameterLiveData)
        activityHomeLiveData = new MutableLiveData<>();
        loadSysParameter(context, 38);
        return activityHomeLiveData;
    }
    public LiveData<SystemParam> getSettingActivity(Context context) {
//        if (null == parameterLiveData)
        activitySettingLiveData = new MutableLiveData<>();
        loadSysParameter(context, 39);
        return activitySettingLiveData;
    }
    public LiveData<SystemParam> getRegDialogTip(Context context) {
//        if (null == parameterLiveData)
        regDialogTipLiveData = new MutableLiveData<>();
        loadSysParameter(context, 34);
        return regDialogTipLiveData;
    }
    public LiveData<SystemParam> getUserTips(Context context) {
//        if (null == parameterLiveData)
        userTipLiveData = new MutableLiveData<>();
        loadSysParameter(context, 30);
        return userTipLiveData;
    }

    private MutableLiveData<Integer> userRoleLiveData;

    //身份 -2：游客、0:普通用户、1:会员用户、2:经销商(团长)、3:运营商
    public LiveData<Integer> getUserRole(Context context){
//        if (null == userRoleLiveData)
            userRoleLiveData = new MutableLiveData<>();
        loadUserRole(context);
        return userRoleLiveData;
    }

    @SuppressLint("HandlerLeak")
    private void loadUserRole(Context context){
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_USER_ROLE;
        request.parser = new NormalIntParse();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                if (response.status == 1) {
                    //身份 -2：游客、0:普通用户、1:会员用户、2:经销商(团长)、3:运营商
                    userRoleLiveData.setValue((Integer) response.responseData);
                } else {
                    ToastUtils.show(response.msg);
                }
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(request));
    }

    public LiveData<SystemParam> getParameter(Context context, int type) {
//        if (null == parameterLiveData)
        parameterLiveData = new MutableLiveData<>();
        loadParameter(context, type);
        return parameterLiveData;
    }
    public LiveData<SystemParam> getSysParameter(Context context, int type) {
//        if (null == parameterLiveData)
        parameterLiveData = new MutableLiveData<>();
        loadSysParameter(context, type);
        return parameterLiveData;
    }

    @SuppressLint("HandlerLeak")
    private void loadParameter(Context context,  int type) {
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_PARAMETER;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("type",type);
        request.reqDataMap = dataMap;
        request.parser = new ParameterDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                SystemParam systemParam = null;
                if (response.status == 1) {
                    systemParam = (SystemParam) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                if (type == 30) {
                    userTipLiveData.setValue(systemParam);
                } else if (type  == 34){
                    regDialogTipLiveData.setValue(systemParam);
                } else if(type == 38){
                    activityHomeLiveData.setValue(systemParam);
                } else if(type == 39){
                    activitySettingLiveData.setValue(systemParam);
                } else {
                    parameterLiveData.setValue(systemParam);
                }
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestPostJSON(new ResponseCallback(request));
    }
    @SuppressLint("HandlerLeak")
    private void loadSysParameter(Context context,  int type) {
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_SYS_PARAMETER;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("",type);
        request.reqDataMap = dataMap;
        request.parser = new ParameterDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                SystemParam systemParam = null;
                if (response.status == 1) {
                    systemParam = (SystemParam) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                if (type == 30) {
                    userTipLiveData.setValue(systemParam);
                } else if (type  == 34){
                    regDialogTipLiveData.setValue(systemParam);
                } else if(type == 38){
                    activityHomeLiveData.setValue(systemParam);
                } else if(type == 39){
                    activitySettingLiveData.setValue(systemParam);
                } else {
                    parameterLiveData.setValue(systemParam);
                }
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(request));
    }
    public LiveData<SystemParam> getCustomer(Context context) {
//        if (null == parameterLiveData)
        parameterLiveData = new MutableLiveData<>();
        loadCustomer(context);
        return parameterLiveData;
    }

    @SuppressLint("HandlerLeak")
    private void loadCustomer(Context context) {
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WECHAT;
        request.parser = new ParameterDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                SystemParam systemParam = null;
                if (response.status == 1) {
                    systemParam = (SystemParam) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                parameterLiveData.setValue(systemParam);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestPostJSON(new ResponseCallback(request));
    }

    public LiveData<OilStationBean> getOilStationDetail(Context context,String gasId) {
//        if (null == oilStationData) {
//        }
        oilStationData = new MutableLiveData<>();
        if (StringUtil.isEmpty(gasId)) {
            return oilStationData;
        }
        loadOilStationDetail(context,gasId);
        return oilStationData;
    }
    @SuppressLint("HandlerLeak")
    private void loadOilStationDetail(Context context,String gasId){
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_STATION_DETAIL;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("",gasId);
        request.reqDataMap = dataMap;
        request.parser = new StationDetailDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                OilStationBean stationBean = null;
                if (response.status == 1) {
                    stationBean = (OilStationBean) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                oilStationData.setValue(stationBean);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(request));
    }

    private MutableLiveData<RefundInfo> refundInfoData;
    public LiveData<RefundInfo> getRefundInfo(Context context,Long orderId) {
        if (null == refundInfoData) {
            refundInfoData = new MutableLiveData<>();
        }
        if (null == orderId) {
            return refundInfoData;
        }
        loadRefundInfo(context,orderId);
        return refundInfoData;
    }
    @SuppressLint("HandlerLeak")
    private void loadRefundInfo(Context context,Long orderId){
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_REFUND_INFO;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("",orderId);
        request.reqDataMap = dataMap;
        request.parser = new RefundInfoDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                RefundInfo stationBean = null;
                if (response.status == 1) {
                    stationBean = (RefundInfo) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                refundInfoData.setValue(stationBean);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(request));
    }

    @SuppressLint("HandlerLeak")
    public void sendRefund(Context context, long orderId, Integer way, String reason, ItemCallback itemCallback) {
        RequestInfo reques = new RequestInfo();
        reques.context = context;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_REFUND;
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reqDateMap.put("id", orderId);
        reqDateMap.put("way", way);
        reqDateMap.put("reason", reason);
        reques.reqDataMap = reqDateMap;
        reques.parser = new NormalIntParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                itemCallback.onItemCallback(0,msg.obj);
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestPostJSON(callback);
    }

    private MutableLiveData<WashOrderDetailBean> washOrderDetailData;
    public LiveData<WashOrderDetailBean> getWashOrderDetail(Context context,Long orderId) {
        if (null == washOrderDetailData) {
            washOrderDetailData = new MutableLiveData<>();
        }
        if (null == orderId) {
            return washOrderDetailData;
        }
        loadWashOrderDetail(context,orderId);
        return washOrderDetailData;
    }
    @SuppressLint("HandlerLeak")
    private void loadWashOrderDetail(Context context,Long orderId){
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_ORDER_DETAIL;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("",orderId);
        request.reqDataMap = dataMap;
        request.parser = new WashOrderDetailDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                WashOrderDetailBean stationBean = null;
                if (response.status == 1) {
                    stationBean = (WashOrderDetailBean) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                washOrderDetailData.setValue(stationBean);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(request));
    }

    private MutableLiveData<WashStationDetailBean> washStoreData;
    public LiveData<WashStationDetailBean> getWashStoreDetail(Context context,String shopCode) {
        if (null == washStoreData) {
            washStoreData = new MutableLiveData<>();
        }
        if (StringUtil.isEmpty(shopCode)) {
            return washStoreData;
        }
        loadWashStoreDetail(context,shopCode);
        return washStoreData;
    }
    @SuppressLint("HandlerLeak")
    private void loadWashStoreDetail(Context context,String shopCode){
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_STATION_DETAIL;
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("",shopCode);
        request.reqDataMap = dataMap;
        request.parser = new WashStationDetailDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                WashStationDetailBean stationBean = null;
                if (response.status == 1) {
                    stationBean = (WashStationDetailBean) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                washStoreData.setValue(stationBean);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(request));
    }

    public LiveData<ParamOilBean> getParamSelect(Context context){
        if (null == paramOilData) {
            paramOilData = new MutableLiveData<>();
            loadParamSelect(context);
        }
        return paramOilData;

    }
    @SuppressLint("HandlerLeak")
    private void loadParamSelect(Context context) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.context = context;
        requestInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_FILTER;
        requestInfo.parser = new ParamOilDataParser();
        requestInfo.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                ParamOilBean stationBeans = null;
                if (response.status == 1) {
                    stationBeans = (ParamOilBean) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                paramOilData.setValue(stationBeans);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(requestInfo);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(requestInfo));

    }
    public LiveData<ParamWashBean> getParamWash(Context context){
        if (null == paramWashData) {
            paramWashData = new MutableLiveData<>();
            loadParamWash(context);
        }
        return paramWashData;

    }
    @SuppressLint("HandlerLeak")
    private void loadParamWash(Context context) {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.context = context;
        requestInfo.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_WASH_FILTER;
        requestInfo.parser = new ParamWashDataParser();
        requestInfo.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                ParamWashBean stationBeans = null;
                if (response.status == 1) {
                    stationBeans = (ParamWashBean) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                paramWashData.postValue(stationBeans);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(requestInfo);
        ReqUtil.getInstance().requestGetJSON(new ResponseCallback(requestInfo));

    }

    public LiveData<List<OilStationBean>> getStationList(Context context,HashMap<String,Object> dataMap){
//        if (null == stationListData) {
//        }
        stationListData = new MutableLiveData<>();
        loadStationList(context,dataMap);
        return stationListData;
    }

    @SuppressLint("HandlerLeak")
    private void loadStationList(Context context,HashMap<String,Object> dataMap) {
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_STATION_LIST;
        request.reqDataMap = dataMap;
        request.parser = new StationListDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                List<OilStationBean> stationBeans = null;
                if (response.status == 1) {
                    stationBeans = (List<OilStationBean>) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                stationListData.postValue(stationBeans);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestPostJSON(new ResponseCallback(request));
    }

    public LiveData<List<WashStationBean>> getWashStationList(Context context,HashMap<String,Object> dataMap){
//        if (null == washStationListData) {
//        }
        washStationListData = new MutableLiveData<>();
        loadWashStationList(context,dataMap);
        return washStationListData;
    }

    @SuppressLint("HandlerLeak")
    private void loadWashStationList(Context context,HashMap<String,Object> dataMap) {
        RequestInfo request = new RequestInfo();
        request.context = context;
        request.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_WASH_STATION_LIST;
        request.reqDataMap = dataMap;
        request.parser = new WashStationListDataParser();
        request.handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ResponseInfo response = (ResponseInfo)msg.obj;
                List<WashStationBean> stationBeans = null;
                if (response.status == 1) {
                    stationBeans = (List<WashStationBean>) response.responseData;
                } else {
                    ToastUtils.show(response.msg);
                }
                washStationListData.postValue(stationBeans);
                super.handleMessage(msg);
            }
        };
        ReqUtil.getInstance().setReqInfo(request);
        ReqUtil.getInstance().requestPostJSON(new ResponseCallback(request));
    }


    /**
     * 获取banner数据
     * @return
     */
    public LiveData<List<BannerBean>> getBannerList(Context mContext){
//        if (null == bannerListData) {
//        }
        bannerListData = new MutableLiveData<List<BannerBean>>();
        loadBanners(mContext);
        return bannerListData;
    }

    @SuppressLint("HandlerLeak")
    private void loadBanners(Context mContext) {
        RequestInfo reques = new RequestInfo();
        reques.context = mContext;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_BANNER;
        reques.parser = new BannerDataParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                List<BannerBean> myTeamData = null;
                if (resInfo.status ==1 ){
                    myTeamData = (List<BannerBean>) resInfo.responseData;
                }else {
                    ToastUtils.show(resInfo.msg);
                }
                bannerListData.setValue(myTeamData);
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestGetJSON(callback);
    }

    private MutableLiveData<List<String>> notifyListLiveData;
    /**
     * 获取Notify数据
     * @return
     */
    public LiveData<List<String>> getNotifyList(Context mContext){
//        if (null == notifyListLiveData) {
//        }
        notifyListLiveData = new MutableLiveData<List<String>>();
        loadNotifyList(mContext);
        return notifyListLiveData;
    }

    @SuppressLint("HandlerLeak")
    private void loadNotifyList(Context mContext) {
        RequestInfo reques = new RequestInfo();
        reques.context = mContext;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_NOTIFY_LIST;
        reques.parser = new NormalStringListParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                List<String> data = null;
                if (resInfo.status ==1 ){
                    data = (List<String>) resInfo.responseData;
                }else {
                    ToastUtils.show(resInfo.msg);
                }
                notifyListLiveData.setValue(data);
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestGetJSON(callback);
    }
    private MutableLiveData<Integer> notifyNumLiveData;
    /**
     * 获取Notify数据
     * @return
     */
    public LiveData<Integer> getNotifyNum(Context mContext){
//        if (null == notifyNumLiveData) {
//        }
        notifyNumLiveData = new MutableLiveData<Integer>();
        loadNotifyNum(mContext);
        return notifyNumLiveData;
    }

    @SuppressLint("HandlerLeak")
    private void loadNotifyNum(Context mContext) {
        RequestInfo reques = new RequestInfo();
        reques.context = mContext;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_MESSAGE_NUM;
        reques.parser = new NormalIntParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                Integer data = null;
                if (resInfo.status ==1 ){
                    data = (Integer) resInfo.responseData;
                }else {
                    ToastUtils.show(resInfo.msg);
                }
                notifyNumLiveData.setValue(data);
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestGetJSON(callback);
    }

    private MutableLiveData<List<List<CategoryBean>>> categoryListData;//首页类别列表数据

    /**
     * 获取类型列表数据
     * @return
     */
    public LiveData<List<List<CategoryBean>>> getCategoryList(Context mContext) {
//        if (null == categoryListData) {
//        }
        categoryListData = new MutableLiveData<>();
        loadCategorys(mContext);
        return categoryListData;
    }

    @SuppressLint("HandlerLeak")
    private void loadCategorys(Context mContext) {
        RequestInfo reques = new RequestInfo();
        reques.context = mContext;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GET_CATEGORY;
        reques.parser = new CategoryDataParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                List<List<CategoryBean>> myTeamData = null;
                if (resInfo.status ==1 ){
                    myTeamData = (List<List<CategoryBean>>) resInfo.responseData;

                }else {
                    ToastUtils.show(resInfo.msg);
                }
                categoryListData.setValue(myTeamData);
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestGetJSON(callback);
    }


    @SuppressLint("HandlerLeak")
    public void sendOilPayInfo(Context context) {
        RequestInfo reques = new RequestInfo();
        reques.context = context;
        reques.reqUrl = HttpConfig.HOST + HttpConfig.INTERFACE_GAS_NOTIFIED;
        HashMap<String, Object> reqDateMap = new HashMap<>();
        reques.reqDataMap = reqDateMap;
        reques.parser = new NormalIntParse();
        reques.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                ResponseInfo resInfo = (ResponseInfo) msg.obj;
                super.handleMessage(msg);
            }
        };
        ResponseCallback callback = new ResponseCallback(reques);
        ReqUtil.getInstance().setReqInfo(reques);
        ReqUtil.getInstance().requestPostJSON(callback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();


    }
}