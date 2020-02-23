package com.cognizant.ford.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cognizant.ford.constant.SearchConstants;
import com.cognizant.ford.utils.HttpClientUtil;
import com.cognizant.ford.utils.ZsetOpsUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
public class SearchController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ZsetOpsUtil setOpsUtil;

    @GetMapping("/search")
    @ApiOperation(value = "查询接口")
    public String searchPostion(@RequestParam String keywords, HttpServletRequest request, Model model) {
        String user = (String) request.getSession().getAttribute("user");

        if (null == user || "" == user) {
            return "/login";
        }

        log.info("用户：" + user + "正在使用查询服务！");

        setOpsUtil.setAdd(SearchConstants.SET_KEY, user);

        StringBuilder builder = new StringBuilder(SearchConstants.INPUT_LOCATION_SEARCH_URL);
        String inputUrl = builder
                .append("&keywords=")
                .append(keywords)
                .toString();
        builder.setLength(0);
        JSONArray tips = JSONObject.parseArray(HttpClientUtil.httpGet(inputUrl).get("tips").toString());
        JSONObject jsonObject = tips.getJSONObject(0);

        Object userLocation = jsonObject.get("location");

        String gasSearchUrl = builder
                .append(SearchConstants.RADIUS_SEARCH_URL)
                .append(userLocation)
                .append("&keywords=")
                .append(SearchConstants.GAS_KERYWORD)
                .toString();
        builder.setLength(0);
        JSONObject respMsg = HttpClientUtil.httpGet(gasSearchUrl);
        if (null == respMsg || null ==respMsg.get("pois")){
            model.addAttribute("result","当前输入位置不精确，查无结果。请重新输入！");
            return "/result";
        }

        JSONArray gasArray = JSONArray.parseArray(respMsg.get("pois").toString());
        String gasName;
        String gasAddress;
        if (gasArray.size() == 0) {
            gasName = "当前5km暂查不到加油站!!!";
            gasAddress = "";
        } else {
            gasName = JSONObject.toJSONString(gasArray.getJSONObject(0).get("name"));
            gasAddress = JSONObject.toJSONString(gasArray.getJSONObject(0).get("address"));
        }

        String ford4SSearchUrl = builder
                .append(SearchConstants.RADIUS_SEARCH_URL)
                .append(userLocation)
                .append("&keywords=")
                .append(SearchConstants.FORD4S_KERYWORD)
                .toString();
        builder.setLength(0);
        JSONArray ford4SArray = JSONArray.parseArray(HttpClientUtil.httpGet(ford4SSearchUrl).get("pois").toString());
        String ford4SName;
        String ford4SAddress;
        if (ford4SArray.size() == 0) {
            ford4SName = "当前5km暂查不到福特4S店!!!";
            ford4SAddress = "";
        }else {
            ford4SName = JSONObject.toJSONString(ford4SArray.getJSONObject(0).get("name"));
            ford4SAddress = JSONObject.toJSONString(ford4SArray.getJSONObject(0).get("address"));
        }

        String key = StringUtils.join("[", gasName, ",", gasAddress, ",", ford4SName, ",", ford4SAddress, "]");
        if (setOpsUtil.zsetRank(user, key) == null) {
            setOpsUtil.zsetAdd(user, key, 0);
        }
        setOpsUtil.zsetIncrScore(user, key, 1.0);

        model.addAttribute("result", "[加油站信息：" + gasName + "---" + gasAddress + "];[福特4S店信息：" + ford4SName + "---" + ford4SAddress + "]");
        return "/result";
    }

    @GetMapping("/showAll")
    @ApiOperation(value = "查询每个用户查询最频繁的加油站和4S店")
    public String showAll(HttpServletRequest request, Model model) {
        String user = (String) request.getSession().getAttribute("user");

        if (null == user || "" == user) {
            return "/login";
        }
        log.info("用户：" + user + "正在使用查看查询历史服务！");

        Set<String> users = setOpsUtil.setMembers(SearchConstants.SET_KEY);
        List<Object> resultList = new ArrayList<>();
        for (String us : users) {
            Map map = new HashMap<String, String>();
            String result = CollectionUtils.lastElement(setOpsUtil.zsetRevRange(us, 0, 0));
            map.put("user", us);
            map.put("result", result);
            resultList.add(map);
        }
        model.addAttribute("resultList", resultList);
        return "/show_all";
    }

}
