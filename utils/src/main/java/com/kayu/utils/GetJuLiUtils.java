package com.kayu.utils;

import java.math.BigDecimal;

/**
 * Author by killer, Email xx@xx.com, Date on 2020/10/22.
 * PS: Not easy to write code, please indicate.
 */
public class GetJuLiUtils {
    /**
     * 根据两个经纬度计算距离km
     * @return
     */
    public static double distance(double latitude,double longitude,double latitude2,double longitude2){
//        double lat1 = Double.parseDouble(lat1Str);
//        double lng1 = Double.parseDouble(lng1Str);
//        double lat2 = Double.valueOf(lat2Str);
//        double lng2 = Double.valueOf(lng2Str);
        double a1 = Math.pow(Math.sin((latitude - Math.abs(latitude2)) * Math.PI / 180 / 2),2);
        double a2 = Math.cos(latitude * Math.PI / 180 );
        double a3 = Math.cos(Math.abs(latitude2) * Math.PI / 180);
        double a4 = Math.pow(Math.sin((longitude - longitude2) * Math.PI / 180 / 2), 2);
        double result =  EARTH_RADIUS * 2 * Math.asin(Math.sqrt(a1 + a2 * a3 * a4));
        return new BigDecimal(result/1000).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    private static final double EARTH_RADIUS = 6378137.0;
    public static double getDistance(double longitude,double latitue,double longitude2,double latitue2){
        double lat1 = rad(latitue);
        double lat2 = rad(latitue2);
        double a = lat1 - lat2;
        double b = rad(longitude)-rad(longitude2);
        double s = 2*Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(b/2),2)));
        s=s*EARTH_RADIUS;
        s=Math.round(s*10000)/10000;
        return new BigDecimal(s/1000).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    private static double rad(double d){
        return d*Math.PI/180.0;
    }
}
