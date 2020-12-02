package com.kayu.car_owner_pay.model;

public class CategoryBean {
    /**
     * "icon": "https://www.ky808.cn/images/20200514/24ae2f6d0b054404bf2907e719eff49d.jpg",
     *             "id": 1,
     *             "tag": "热门推荐",
     *             "href": null,
     *             "title": "金融技术"
     */
    public long id;//主键
    public String title;//主键
    public String icon;//图标加载url
    public String tag;//小标签
    public String remark;//小标题
    public String href;//H5跳转链接
    //"type": "KY_H5"
    public String type;//跳转类型


}
