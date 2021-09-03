package com.kayu.car_owner_pay.model;

public class SysOrderBean {
    /**
     * {
     * "icon": "https://www.kakayuy.com/group1/M00/00/04/rBoO71-9_hOAalibAAAOpjQmAxE902.png",
     * "isPublic": 0,
     * "id": 1,
     * "href": null,
     * "sort": "1-1",
     * "title": "加油订单",
     * "type": "KY_GAS"
     * }
     */
    public long id;//主键
    public String title;//主键
    public String icon;//图标加载url
    public String href;//H5跳转链接
    //"type": "KY_H5"
    public String type;//跳转类型
    public String sort;//排序
    public Integer isPublic; //是否公开
}
