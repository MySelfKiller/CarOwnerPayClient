package com.kayu.car_owner_pay.model;

import java.util.List;

public class OilStationBean {
    /**
     * "id": 1,
     * "gasId": "ZF223110356",
     * "gasName": "众诚连锁卫星加油站",
     * "gasLogoSmall": "https://static.czb365.com/gas_images/ZF223110356_small.jpg?x-oss-process=image/resize,m_lfit,h_200,w_200/format,png",
     * "gasAddress": "吉林省长春市朝阳区卫星路7630号",
     * "gasAddressLatitude": 43.832146,
     * "gasAddressLongitude": 125.307571,
     * "distance": 0.0,
     * "priceYfq": 6.13,
     * "priceOfficial": 7.31,
     * "priceGun": 7.11
     */

    public Long id;//主键
    public String gasId;//油站API主键
    public String gasName;   //油站名称
    public String gasLogoSmall;   //油站压缩图标
    public String gasAddress;   //油站详细地址
    public double gasAddressLatitude;   //油站纬度
    public double gasAddressLongitude;   //油站经度
    public double distance;   //距离/km
    public double priceYfq;   //油团价/元
    public double priceOfficial;     //国标价/元
    public double priceGun;     //枪价/元
    public String offDiscount;//国标折扣/百分比
    public String gunDiscount;//油站折扣/百分比
    public List<OilsTypeParam> oilsTypeList;
}
