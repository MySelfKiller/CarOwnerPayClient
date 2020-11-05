package com.kayu.utils.location;

import com.amap.api.location.AMapLocation;

public interface LocationCallback {
    void onLocationChanged(AMapLocation location);
}
