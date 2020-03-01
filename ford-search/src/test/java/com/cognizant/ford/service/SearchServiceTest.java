package com.cognizant.ford.service;

import com.cognizant.ford.SearchApplication;
import com.cognizant.ford.domain.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SearchApplication.class})
@WebAppConfiguration
@FixMethodOrder(MethodSorters.JVM)
public class SearchServiceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static final List<GrantedAuthority> AUTHORITIES = new ArrayList<>();


    @Before
    public void setUpMockMvc() {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new GithubUser("TT"),
                null, AUTHORITIES));
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    //查询服务
    @Test
    public void testSearchService() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get( "/ford/search");
        builder.param("keywords", "妙栏小区");
        MvcResult mvcResult;
        try {
            mvcResult = mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    //查询历史
    @Test
    public void testSearchHistory() {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get( "/ford/searchHistory");
        MvcResult mvcResult;
        try {
            mvcResult = mockMvc.perform(builder)
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();
            String result = mvcResult.getResponse().getContentAsString();
            Assert.assertNotNull(result);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }}
