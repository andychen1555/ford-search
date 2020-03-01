package com.cognizant.ford.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cognizant.ford.constant.SearchConstants;
import com.cognizant.ford.entity.SearchRecord;
import com.cognizant.ford.repository.SearchRepository;
import com.cognizant.ford.service.SearchService;
import com.cognizant.ford.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    SearchRepository searchRepository;

    @Transactional
    @Override
    public String searchPostion(String keywords, String user, Model model) throws Exception {

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
        if (null == respMsg || null == respMsg.get("pois")) {
            model.addAttribute("result", "当前输入位置不精确，查无结果。请重新输入！");
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
        } else {
            ford4SName = JSONObject.toJSONString(ford4SArray.getJSONObject(0).get("name"));
            ford4SAddress = JSONObject.toJSONString(ford4SArray.getJSONObject(0).get("address"));
        }

        SearchRecord oldRecord = CollectionUtils.lastElement(searchRepository.findByUserAndAndGasNameAndGasAddressAndFord4SNameAndFord4SAddress(
                user, gasName, gasAddress, ford4SName, ford4SAddress));

        if (null == oldRecord) {
            SearchRecord searchRecord = SearchRecord.builder().user(user)
                    .gasName(gasName)
                    .gasAddress(gasAddress)
                    .ford4SName(ford4SName)
                    .ford4SAddress(ford4SAddress)
                    .numbers(1L)
                    .build();
            //插入新纪录
            searchRepository.save(searchRecord);
        } else {
            //更新numbers
            oldRecord.setNumbers(oldRecord.getNumbers() + 1L);
            searchRepository.save(oldRecord);
        }

        model.addAttribute("result", "[加油站信息：" + gasName + "---" + gasAddress + "];[福特4S店信息：" + ford4SName + "---" + ford4SAddress + "]");

        return "/result";
    }

    @Override
    public List<SearchRecord> queryHistory() throws Exception {
        return searchRepository.queryHistory();
    }
}
