package com.cognizant.ford.authentication;

import com.alibaba.fastjson.JSONObject;
import com.cognizant.ford.domain.GithubUser;
import com.cognizant.ford.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GithubAuthenticationManager implements AuthenticationManager {

    private static final List<GrantedAuthority> AUTHORITIES = new ArrayList<>();

    /**
     * 获取 GitHub 登录信息的 API 地址
     */
    private final static String userInfoUri = "https://api.github.com/user";

    /**
     * 获取 GitHub 用户信息的地址拼接
     */
    private final static String USER_INFO_API = "%s?access_token=%s";

    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.getName() != null && auth.getCredentials() != null) {
            SecurityContextHolder.getContext().setAuthentication(auth);
            GithubUser user = getUserInfo(auth.getName());
            return new UsernamePasswordAuthenticationToken(user,
                    null, AUTHORITIES);
        }
        throw new BadCredentialsException("Bad Credentials");
    }

    private GithubUser getUserInfo(String accessToken) {
        String url = String.format(USER_INFO_API, userInfoUri, accessToken);
        JSONObject info;
        try {
            info = HttpClientUtil.httpGet(url);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("GitHub 登录失败！！", e.getMessage()));
            return null;
        }
        GithubUser user = new GithubUser();
        user.setUsername(info.getString("name"));
        return user;
    }
}
