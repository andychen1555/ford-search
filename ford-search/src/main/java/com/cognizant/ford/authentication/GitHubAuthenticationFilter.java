package com.cognizant.ford.authentication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cognizant.ford.domain.GitHubToken;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.net.ssl.SSLHandshakeException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class GitHubAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final static String CODE = "code";
    /**
     * 获取 Token 的 API
     */
    private final static String accessTokenUri = "https://github.com/login/oauth/access_token";

    /**
     * grant_type 由github提供
     */
    private final static String grantType = "authorization_code";

    /**
     * client_id 由github提供
     */
    private static final String clientId = "72bb17cf774708116850";

    /**
     * client_secret 由github提供
     */
    private final static String clientSecret = "aa150be7c59b46303c58b1a516ec6dd583502957";

    /**
     * redirect_uri github回调地址
     */
    private final static String redirectUri = "http://localhost:8080/login/github";

    /**
     * 获取 OpenID 的 API 地址
     */
    private final static String openIdUri = "https://api.github.com/user?access_token=";

    /**
     * 获取 token 的地址拼接
     */
    private final static String TOKEN_ACCESS_API = "%s?grant_type=%s&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";

    public GitHubAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl, "GET"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String code = request.getParameter(CODE);
        String tokenAccessApi = String.format(TOKEN_ACCESS_API, accessTokenUri, grantType, clientId, clientSecret, code, redirectUri);
        GitHubToken gitHubToken = this.getToken(tokenAccessApi);
        log.info("github token::" +gitHubToken);
        if (gitHubToken != null){
            String openId = getOpenId(gitHubToken.getAccessToken());
            if (openId != null){
                // 生成验证 authenticationToken
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(gitHubToken.getAccessToken(), openId);
                // 返回验证结果
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        }
        return null;
    }

    private GitHubToken getToken(String tokenAccessApi) throws IOException{
        Document document = Jsoup.connect(tokenAccessApi).ignoreContentType(true).get();
        String tokenResult = document.text();
        String[] results = tokenResult.split("&");

        GitHubToken gitHubToken = new GitHubToken();
        String accessToken = results[0].replace("access_token=", "");
        gitHubToken.setAccessToken(accessToken);
        return gitHubToken;
    }

    private String getOpenId(String accessToken) throws IOException{
        String url = openIdUri + accessToken;
        Document document;
        try {
            document = Jsoup.connect(url).ignoreContentType(true).get();
        }catch (SSLHandshakeException sslEx){
            sslEx.printStackTrace();
            log.error(String.format("GitHub 服务器异常,%s ！！！！！！！！！！！！",sslEx.getMessage()));
            return null;
        }catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("GitHub 登录失败！！",e.getMessage()));
            return null;
        }
        String resultText = document.text();
        JSONObject result = JSON.parseObject(resultText);
        if(null != result.get("id")){
            return String.valueOf(result.get("id"));
        }
        return null;
    }

}
