package com.example.sso_resource_server.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 安全调试模式开关，从配置读取，生产环境应设为false
     */
    @Value("${security.debug:false}")
    private boolean securityDebug;

    @Override
    public void init(WebSecurity web) throws Exception {
        // 根据配置设置调试模式
        web.debug(securityDebug);
        super.init(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                // CSRF保护已禁用，原因如下：
                // 1. 使用JWT Bearer Token认证，Token存储在Authorization Header中，不依赖Cookie
                // 2. Session策略为STATELESS，不创建服务器端Session
                // 3. 浏览器不会自动发送Bearer Token，CSRF攻击无法获取Token
                // 注意：如果未来添加基于Cookie的认证或Session管理，需要重新评估并启用CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(req -> req
                        .antMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                //oauth2使用jwt模式，会走接口拿取公钥解密验签
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt()
                        //读取权限默认读取scopes，这里配置一个转换器，使其也读取Authorities
                        .jwtAuthenticationConverter(customJwtAuthenticationTokenConverter())
                );
    }

    private Converter<Jwt, AbstractAuthenticationToken> customJwtAuthenticationTokenConverter() {
        return jwt -> {
            List<String> userAuthorities = jwt.getClaimAsStringList("authorities");
            List<String> scopes = jwt.getClaimAsStringList("scope");
            List<GrantedAuthority> combinedAuthorities = Stream.concat(
                            userAuthorities.stream(),
                            //加上 SCOPE_前缀
                            scopes.stream().map(scope -> "SCOPE_" + scope))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            String username = jwt.getClaimAsString("user_name");
            return new UsernamePasswordAuthenticationToken(username, null, combinedAuthorities);
        };
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/error",
                        "/resources/**",
                        "/static/**",
                        "/public/**",
                        "/h2-console/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v2/api-docs/**",
                        "/doc.html",
                        "/swagger-resources/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
