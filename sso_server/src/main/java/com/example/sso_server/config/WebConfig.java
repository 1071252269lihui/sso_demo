package com.example.sso_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 */
@SuppressWarnings("deprecation")
@Configuration
public class WebConfig    {

    /**
     * JWT密钥库密码，从环境变量或配置文件读取
     */
    @Value("${jwt.keystore.password:lihui23}")
    private String keystorePassword;

    /**
     * JWT密钥库别名，从环境变量或配置文件读取
     */
    @Value("${jwt.keystore.alias:auth-jwt}")
    private String keystoreAlias;

    /**
     * 编码器创建
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(){

        //默认编码算法的Id,新的密码编码都会使用这个id对应的编码器
        String idForEncode = "bcrypt";
        //要支持的多种编码器
        //举例：历史原因，之前用的SHA-1编码，现在我们希望新的密码使用bcrypt编码
        //老用户使用SHA-1这种老的编码格式，新用户使用bcrypt这种编码格式，登录过程无缝切换
        Map<String,PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode,new BCryptPasswordEncoder());
        //encoders.put("SHA-1",new MessageDigestPasswordEncoder("SHA-1"));

        //（默认编码器id，编码器map）
        return new DelegatingPasswordEncoder(idForEncode,encoders);
    }

    @Bean
    public KeyPair keyPair() {
        //获取资源文件中的密钥
        ClassPathResource ksFile = new ClassPathResource("jwt.jks");
        //输入密码创建KeyStoreKeyFactory，密码从配置读取
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, keystorePassword.toCharArray());
        //通过别名获取KeyPair，别名从配置读取
        return ksFactory.getKeyPair(keystoreAlias);
    }
}
