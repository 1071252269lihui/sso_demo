package com.example.sso_server;

import com.example.sso_server.daoGenerate.EntityGeneratorDemo1;
import com.example.sso_server.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.Resource;
@Slf4j
@SpringBootTest
class SsoServerApplicationTests {

	@Resource
	private UserDetailsServiceImpl userDetailsService;

	@Test
	void contextLoads() {
	}

	@Test
	public void generate() throws Exception {
		EntityGeneratorDemo1.generate();
	}

	@Test
	public void loadUserByUsername() throws Exception {
		UserDetails details = userDetailsService.loadUserByUsername("test");
		log.info(details.toString());
	}

}
