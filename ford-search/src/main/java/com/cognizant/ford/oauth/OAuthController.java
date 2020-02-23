package com.cognizant.ford.oauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.net.ssl.SSLHandshakeException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

@Controller
@RequestMapping("/OAuth")
@Slf4j
public class OAuthController {

    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    @Value("${spring.application.name}")
    private String appName;
    @Value("${github.appId}")
    private String appId;
    @Value("${github.appSecret}")
    private String appSecret;
    @Value("${github.callbackUrl}")
    private String callbackUrl;
    @Value("${github.redrictUrl}")
    private String redrictUrl;

    @RequestMapping(value = "/authLogin", method = RequestMethod.GET)
    public void authLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect(redrictUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/callback/getOAuth", method = RequestMethod.GET)
    public String getOAuth(@RequestParam(value = "code", required = true) String code,
                           HttpServletRequest request, HttpServletResponse response) {
        String secretState = "secret" + new Random().nextInt(999_999);
        OAuth20Service service = new ServiceBuilder(appId)
                .apiSecret(appSecret).state(secretState)
                .callback(callbackUrl).build(GitHubApi.instance());
        HttpSession session = request.getSession();
        try {
            OAuth2AccessToken  accessToken = service.getAccessToken(code);
            final OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, oAuthRequest);
            final Response oAuthresponse = service.execute(oAuthRequest);
            JSONObject o = JSON.parseObject(oAuthresponse.getBody());
            session.setAttribute("user",String.valueOf(o.get("name")));
            log.info("callback/getOAuth 登录user是：  " + o.get("name"));
        }catch (SSLHandshakeException sslException){
            log.error(String.format("GitHub 服务器异常,%s ！！！！！！！！！！！！",sslException.getMessage()));
            return "login";
        }catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("GitHub 登录失败！！",e.getMessage()));
            return "login";
        }
        return "login_success";
    }

}
