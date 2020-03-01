package com.cognizant.ford.service;

import com.cognizant.ford.entity.SearchRecord;
import org.springframework.ui.Model;

import java.util.List;

public interface SearchService {

    String searchPostion(String keywords, String user, Model model) throws Exception;

    List<SearchRecord> queryHistory()throws Exception;
}
