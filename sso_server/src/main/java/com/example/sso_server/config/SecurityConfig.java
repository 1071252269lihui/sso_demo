package com.example.sso_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 安全调试模式开关，从配置读取，生产环境应设为false
     */
    @Value("${security.debug:false}")
    private boolean securityDebug;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        // 根据配置设置调试模式
        web.debug(securityDebug);
        super.init(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //授权模式登陆页面,这里采用默认的登陆页面
                .formLogin()
                .and()
                .httpBasic()
                .and()
//                .csrf(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable)
                // 授权模式这里采用默认的页面，登陆成功后从session中拿取回调url
                //.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(req -> req
                        //oauth2 的接口在 /oauth 接口下面，这里配置其不走SpringSecurity的认证流程
                        .antMatchers("/oauth/**").permitAll()
                        .anyRequest().authenticated()
                ) ;
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/error",
                        "/resources/**",
                        "/static/**",
                        "/templates/**",
                        "/public/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
