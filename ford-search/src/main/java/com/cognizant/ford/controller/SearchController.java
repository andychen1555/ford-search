package com.cognizant.ford.controller;


import com.cognizant.ford.domain.GithubUser;
import com.cognizant.ford.entity.SearchRecord;
import com.cognizant.ford.service.SearchService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/ford")
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/search")
    @ApiOperation(value = "根据 keywords 查询加油站和福特4S店")
    public String searchPostion(@RequestParam String keywords,Model model) {

        GithubUser githubUser = (GithubUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (null == githubUser) {
            return "/login";
        }

        String user = githubUser.getUsername();
        log.info("用户：" + user + "正在使用查询服务！");

        try{
            return searchService.searchPostion(keywords, user, model);
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.format("searchPostion interface error!!!",e.getMessage()));
            model.addAttribute("errorMsg", e.getMessage());
            return "/error";
        }
    }

    @GetMapping("/searchHistory")
    @ApiOperation(value = "查询每个用户查询最频繁的加油站和4S店")
    public String searchHistory(Model model) {
        GithubUser githubUser = (GithubUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (null == githubUser) {
            return "/login";
        }
        String user = githubUser.getUsername();
        log.info("用户：" + user + "正在使用查看查询历史服务！");

        try {
            List<SearchRecord> resultList = searchService.queryHistory();
            model.addAttribute("resultList", resultList);
            return "/show_all";
        }catch (Exception e){
            e.printStackTrace();
            log.error(String.format("searchHistory interface error!!!",e.getMessage()));
            model.addAttribute("errorMsg", e.getMessage());
            return "/error";
        }
    }

}
