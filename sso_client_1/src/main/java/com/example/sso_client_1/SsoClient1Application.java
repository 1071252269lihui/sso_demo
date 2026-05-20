package com.example.sso_client_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@SpringBootApplication
@EnableOAuth2Sso
@EnableZuulProxy
public class SsoClient1Application   extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(SsoClient1Application.class, args);
	}


  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
      .logout()
      .logoutSuccessUrl("/")
      .and().authorizeRequests()
      .antMatchers("/index.html", "/", "/login")
      .permitAll().anyRequest().authenticated()
      .and()
      .csrf()
      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    ;
  }
  @Override
  public void configure(WebSecurity web) {
    web
      .ignoring()
      .antMatchers("/error",
        "/resources/**",
        "/static/**",
        "/public/**",
        "/favicon.*",
        "/*/icon-*",
        "/**.js",
        "/**.css");
  }
}
