package com.cognizant.ford.constant;

public class SearchConstants {
    public static final String SET_KEY = "FordSearch";
    public static final String GAS_KERYWORD = "中石化加油站";
    public static final String FORD4S_KERYWORD = "福特4S店";
    public static final String AMAP_KEY = "3508e405e931b64dc85dad74ac080df9";
    public static final String INPUT_LOCATION_SEARCH_URL = "https://restapi.amap.com/v3/assistant/inputtips?" +
            "&key=" + AMAP_KEY;
    public static final String RADIUS_SEARCH_URL = "https://restapi.amap.com/v3/place/around?" +
            "&key=" + AMAP_KEY + "&radius=5000&location=";
}
