package com.kayu.car_owner_pay.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WashOrderDetailBean {

    /**
     * useExplain : https://www.kakayuy.com/group1/M00/00/04/rBoO71-SoHOAWUIkAAMyjhPrIOg605.png
     * id : 55
     * orderNo : an30H5777tdS8h8aZ0268261z0u9C066
     * shopCode : 131203001
     * shopName : 名都洗车店
     * address : 河北省唐山市曹妃甸区新城大街兴海名都地下停车场D区
     * longitude : 118.45866
     * latitude : 39.283637
     * telephone : 13403259609
     * doorPhotoUrl :
     * doorImgList : null
     * serviceCode : 81
     * serviceName : 标准洗车-五座轿车
     * amount : 29
     * finalPrice : 0.01
     * realAmount : 0.01
     * state : 1
     * cmpTime : 2020-10-15 14:32:17
     * createTime : 2020-10-12 10:38:50
     * modifyTime : 2020-10-15 14:32:22
     * busTime : 08:00-18:00
     * serOrderNo : 1023SDXCNFR2010119856
     * effTime : 2020-11-10
     * qrCodeBase64 : data:image/PNG;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAIAAACzY+a1AAACIUlEQVR42u3aQY7EMAgEwPz/07tviEwDzlRfI2Ucag4I/PzJ5XmUAKEgFIQIBaEgFIQIBaEglDDh05XC331Xha5TJT4BIUKECBEiRPj+Cyu7qTdvbvM+edpWDYQIESJEiBBhHWFbsaa8l7wZIUKECBEiRHghYduQrLBvRIgQIUKECBEizKRwUYcQIUKECBEi/BxhbkOWu++UW1ta+SJEiBAhQoQ3EOZyAjz1tK0aCBEiRIgQIcIDwp2ZuqS0rg4IESJEiBAhwmyxTnrdtkaxbd52x8oXIUKECBEi/CBhbsK0BHiqix4bsCFEiBAhQoQ/RzjVoS2pbI6h5PMRIkSIECFChOE+Koe05BjjQYgQIUKECBH2joVuXBDm7kotvZCPECFChAgRfpBwp8oVc74cP0KECBEiRIhwZUeau2S9JN8fsCFEiBAhQoRLCU/aucLOMDdRKyx0/20ohAgRIkSIEGF4hJabqLXtKXMd6ZaVL0KECBEiRPh9wqksmbdtWxAiRIgQIUKECMP7wraGra3uhVPAkokaQoQIESJEiPCYsK3nLDxG2/qwbWSIECFChAgRIqwj7D90erL1DGWsI0WIECFChAgRXnCFaap9XbovRIgQIUKECBE2EU7tGu9aeSJEiBAhQoQI79kXTv11pr4IIUKECBEiRBgjnGrndg7ncptIhAgRIkSIEGEdoWwOQoSCUBAiFISCUBAiFISCUBD+cv4B0+tgvbFL9NsAAAAASUVORK5CYII=
     * qrString : 1023SDXCNFR2010119856
     * explain : 使用时请告知门店本券为盛大汽车服务券
     */

    @SerializedName("useExplain")
    public String useExplain;
    @SerializedName("id")
    public Long id;
    @SerializedName("orderNo")
    public String orderNo;
    @SerializedName("shopCode")
    public String shopCode;
    @SerializedName("shopName")
    public String shopName;
    @SerializedName("address")
    public String address;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("telephone")
    public String telephone;
    @SerializedName("doorPhotoUrl")
    public String doorPhotoUrl;
    @SerializedName("doorImgList")
    public List<String> doorImgList;
    @SerializedName("serviceCode")
    public String serviceCode;
    @SerializedName("serviceName")
    public String serviceName;
    @SerializedName("amount")
    public Double amount;
    @SerializedName("finalPrice")
    public Double finalPrice;
    @SerializedName("realAmount")
    public Double realAmount;
    @SerializedName("state")
    public Integer state;
    @SerializedName("cmpTime")
    public String cmpTime;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("modifyTime")
    public String modifyTime;
    @SerializedName("busTime")
    public String busTime;
    @SerializedName("serOrderNo")
    public String serOrderNo;
    @SerializedName("effTime")
    public String effTime;
    @SerializedName("qrCodeBase64")
    public String qrCodeBase64;
    @SerializedName("qrString")
    public String qrString;
    @SerializedName("explain")
    public String explain;
}
