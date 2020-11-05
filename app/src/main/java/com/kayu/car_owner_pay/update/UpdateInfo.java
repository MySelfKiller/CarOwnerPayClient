package com.kayu.car_owner_pay.update;

public class UpdateInfo {
    public String id;
    public String url;//url下载连接
    public String content;//url下载连接
    public int state;
    public int type;
    public long pathLength;//文件总大小
    public String pathMd5;//下载文件校验码
    public int force;//更新状态0无需更新1选择性更新2强制更新

}
