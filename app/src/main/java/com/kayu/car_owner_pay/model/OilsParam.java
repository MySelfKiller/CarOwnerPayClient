package com.kayu.car_owner_pay.model;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/9/28.
 * PS: Not easy to write code, please indicate.
 * "id": null,
 *                         "gasId": null,
 *                         "oilNo": 92,
 *                         "oilName": "92#",
 *                         "priceYfq": 6.13,
 *                         "priceGun": 7.11,
 *                         "priceOfficial": 7.31,
 *                         "oilType": 1,
 *                         "gunNos": "1,3,6"
 * id	Long	主键(备用)
 * gasId	String	Api主键(备用)
 * oilNo	Int	油号编码
 * oilName	String	油号名称
 * priceYfq	Double	团油价/元
 * priceGun	Double	油枪价/元
 * priceOfficial	Double	国标价/元
 * oilType	Int	类型 (备用)
 * gunNos	String	油枪(,)逗号分隔
 */
public class OilsParam {
    public int oilNo;//油号编码
    public String oilName;//油号名称
    public int oilType;//燃油类型 1:汽油 2:柴油 3:天然气
    public int isDefault;//是否为默认条件 0:否 1:是

    public long id;//主键(备用)
    public String gasId;//Api主键(备用)
    public String gunNos;//油枪(,)逗号分隔
    public double priceYfq;//团油价/元
    public double priceGun;//油枪价/元
    public double priceOfficial;//国标价/元

}
