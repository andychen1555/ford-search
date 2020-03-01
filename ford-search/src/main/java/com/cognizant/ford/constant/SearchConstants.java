package com.cognizant.ford.constant;

public class SearchConstants {

    //gas 查询关键字
    public static final String GAS_KERYWORD = "中石化加油站";

    //ford4S 查询关键字
    public static final String FORD4S_KERYWORD = "福特4S店";

    //    public static final String AMAP_KEY = "3508e405e931b64dc85dad74ac080df9";
    //Fan
    //高德API 接入key
    public static final String AMAP_KEY = "d95596e996fa12a74f823c8590c2a6ac";

    //高德API 根据关键字查询 location
    public static final String INPUT_LOCATION_SEARCH_URL = "https://restapi.amap.com/v3/assistant/inputtips?" +
            "&key=" + AMAP_KEY;

    //高德API 根据location查询附近地点
    public static final String RADIUS_SEARCH_URL = "https://restapi.amap.com/v3/place/around?" +
            "&key=" + AMAP_KEY + "&radius=5000&location=";
}
