package com.cognizant.ford.config;

import com.cognizant.ford.authentication.GitHubAuthenticationFilter;
import com.cognizant.ford.authentication.GithubAuthenticationManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/index","/h2-console/**").permitAll()
                .antMatchers("/login_success").hasRole("USER")
                .antMatchers("/ford/**").hasRole("USER")
                .and()
                .formLogin()
                .loginPage("/login").defaultSuccessUrl("/login_success")
                .permitAll();

        http.csrf().ignoringAntMatchers("/h2-console/**"); // 禁用 H2 控制台的 CSRF 防护
        http.headers().frameOptions().sameOrigin(); // 允许来自同一来源的H2 控制台的请求

        // 在 UsernamePasswordAuthenticationFilter 前添加 GitHubAuthenticationFilter
        http.addFilterAt(gitHubAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 自定义 gitHub 登录 过滤器
     */
    private GitHubAuthenticationFilter gitHubAuthenticationFilter(){
        GitHubAuthenticationFilter authenticationFilter = new GitHubAuthenticationFilter("/login/github");
        authenticationFilter.setAuthenticationManager(new GithubAuthenticationManager());
        return authenticationFilter;
    }
}
