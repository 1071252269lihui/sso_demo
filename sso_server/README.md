# SSO Server - OAuth2 授权服务器

基于 Spring Security OAuth2 的 OAuth2 授权服务器，提供完整的认证授权功能，支持 JWT 令牌签发。

## 技术栈

- **Spring Boot**: 2.5.5
- **Spring Security OAuth2**: 2.5.0.RELEASE
- **Spring Security OAuth2 Autoconfigure**: 2.3.0.RELEASE
- **JWT**: RSA 非对称加密签名
- **Fluent-MyBatis**: 1.8.7 (ORM框架)
- **MySQL**: 8.0.28
- **Redis**: Token 持久化存储

## 功能特性

- OAuth2 多种授权模式（授权码、密码、客户端凭证等）
- JWT 令牌签发与验证（RSA 非对称加密）
- JWK Set 端点（供资源服务器获取公钥）
- 用户认证与权限加载
- Token 持久化到 Redis
- 基于数据库的客户端配置管理

## 项目结构

```
sso_server/
├── src/main/java/com/example/sso_server/
│   ├── SsoServerApplication.java           # 启动类
│   ├── config/                              # 配置类
│   │   ├── AuthorizationServerConfig.java   # 授权服务器配置
│   │   ├── SecurityConfig.java              # Spring Security 配置
│   │   ├── WebConfig.java                   # Web 配置（密码编码器、密钥对）
│   │   ├── JwkSetEndpoint.java              # JWK Set 端点
│   │   └── JwkSetEndpointConfiguration.java # JWK 端点安全配置
│   ├── entity/                              # 实体类
│   │   ├── SsUserEntity.java                # 用户实体
│   │   ├── SsRoleEntity.java                # 角色实体
│   │   ├── SsAuthorityEntity.java           # 权限实体
│   │   ├── SsUserRoleRelEntity.java         # 用户-角色关联
│   │   ├── SsAuthorityRoleRelEntity.java    # 权限-角色关联
│   │   └── CustomUserDetailsEntity.java     # 用户详情封装
│   ├── service/                             # 服务层
│   │   └── UserDetailsServiceImpl.java      # 用户详情服务
│   ├── dao/                                 # 数据访问层
│   │   ├── intf/                            # DAO 接口
│   │   └── impl/                            # DAO 实现
│   ├── daoGenerate/                         # 代码生成器
│   │   └── EntityGeneratorDemo1.java        # Fluent-MyBatis 实体生成
│   └── wrapper/                             # 查询包装器（自动生成）
├── src/main/resources/
│   ├── application.yml                      # 主配置文件
│   ├── application.properties               # 调试配置
│   └── jwt.jks                              # JWT 密钥库
└── pom.xml                                  # Maven 配置
```

## 配置说明

### 服务配置

```yaml
server:
  port: 8080
  servlet:
    context-path: /authorization-server

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oauth2server
    username: test
    password: test@2022_12
  redis:
    host: localhost
    port: 6379
```

### 数据库表结构

#### 用户相关表

| 表名 | 说明 |
|------|------|
| `ss_user` | 用户表（id, username, password, status） |
| `ss_role` | 角色表（id, name, desc, create_at） |
| `ss_authority` | 权限表（id, parent_id, name, desc, resource, type, create_at） |
| `ss_user_role_rel` | 用户-角色关联表（id, uid, rid） |
| `ss_authority_role_rel` | 权限-角色关联表（id, authority_id, role_id） |

#### OAuth2 表

| 表名 | 说明 |
|------|------|
| `oauth_client_details` | OAuth2 客户端配置 |
| `oauth_client_token` | 客户端令牌 |
| `oauth_access_token` | 访问令牌 |
| `oauth_refresh_token` | 刷新令牌 |
| `oauth_code` | 授权码 |

## OAuth2 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/oauth/authorize` | GET | 授权端点（授权码模式） |
| `/oauth/token` | POST | 令牌端点 |
| `/oauth/check_token` | POST | 令牌验证端点 |
| `/oauth/token_key` | GET | 令牌密钥端点 |
| `/.well-known/jwks.json` | GET | JWK Set 端点（公钥） |

## 授权模式

### 1. 授权码模式 (Authorization Code)

```bash
# 步骤1: 获取授权码
GET /oauth/authorize?response_type=code&client_id=web-client&redirect_uri=http://localhost:8080/login

# 步骤2: 使用授权码换取令牌
POST /oauth/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&code=AUTH_CODE&redirect_uri=http://localhost:8080/login
```

### 2. 密码模式 (Resource Owner Password Credentials)

```bash
curl -X POST "http://localhost:8080/authorization-server/oauth/token" \
  -H "Authorization: Basic $(echo -n 'web-client:12345678' | base64)" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&username=user&password=password"
```

### 3. 客户端凭证模式 (Client Credentials)

```bash
curl -X POST "http://localhost:8080/authorization-server/oauth/token" \
  -H "Authorization: Basic $(echo -n 'client-id:client-secret' | base64)" \
  -d "grant_type=client_credentials"
```

### 4. 刷新令牌模式 (Refresh Token)

```bash
curl -X POST "http://localhost:8080/authorization-server/oauth/token" \
  -H "Authorization: Basic $(echo -n 'web-client:12345678' | base64)" \
  -d "grant_type=refresh_token&refresh_token=REFRESH_TOKEN"
```

## JWT 令牌结构

```json
{
  "user_name": "admin",
  "scope": ["read", "write"],
  "authorities": ["api:hello", "api:user"],
  "exp": 1700000000,
  "jti": "unique-token-id",
  "client_id": "web-client"
}
```

### JWT 头部

```json
{
  "alg": "RS256",
  "kid": "fHnwaNVNZIepo7XTIMM8KRfB7AjE454m0qcYBlQgJVE"
}
```

## 核心配置类

### AuthorizationServerConfig

授权服务器核心配置：

```java
@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    // 配置客户端详情服务（从数据库读取）
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) {
        clients.jdbc(dataSource);
    }

    // 配置令牌端点
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
            .tokenEnhancer(chain)
            .tokenStore(tokenStore())
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService);
    }

    // JWT 令牌转换器（RSA 签名）
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        return new JwtCustomHeadersAccessTokenConverter(keyPair);
    }

    // Token 持久化（Redis）
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(connectionFactory);
    }
}
```

### SecurityConfig

Spring Security 安全配置：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .formLogin()
            .and()
            .httpBasic()
            .and()
            .authorizeRequests(req -> req
                .antMatchers("/oauth/**").permitAll()
                .anyRequest().authenticated()
            );
    }
}
```

### JwkSetEndpoint

JWK Set 端点，供资源服务器获取公钥验签：

```java
@FrameworkEndpoint
class JwkSetEndpoint {
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getKey() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey)
            .keyIDFromThumbprint()
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(Algorithm.parse("RS256"))
            .build();
        return new JWKSet(key).toJSONObject();
    }
}
```

## 用户认证流程

```
1. 用户提交用户名密码
       ↓
2. UserDetailsServiceImpl.loadUserByUsername()
       ↓
3. 查询 ss_user 表获取用户信息
       ↓
4. 查询用户关联的权限（多表联查）
       ↓
5. 构建 CustomUserDetailsEntity
       ↓
6. 返回 UserDetails 给 Spring Security
```

### 权限查询 SQL

```sql
SELECT a.*, a.resource as 'authority'
FROM ss_user u
LEFT JOIN ss_user_role_rel ur ON u.id = ur.uid
LEFT JOIN ss_role r ON ur.rid = r.id
LEFT JOIN ss_authority_role_rel ar ON r.id = ar.role_id
LEFT JOIN ss_authority a ON ar.authority_id = a.id
WHERE u.id = ? AND a.type = 1
GROUP BY a.id
```

## 密钥管理

### 密钥库配置

```java
@Bean
public KeyPair keyPair() {
    ClassPathResource ksFile = new ClassPathResource("jwt.jks");
    KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, "password".toCharArray());
    return ksFactory.getKeyPair("auth-jwt");
}
```

### 密钥库信息

- 文件: `jwt.jks`
- 别名: `auth-jwt`
- 算法: RSA 2048-bit

## 快速开始

### 前置条件

1. JDK 8+
2. Maven 3.6+
3. MySQL 8.0+
4. Redis

### 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE oauth2server;

-- 创建用户表
CREATE TABLE ss_user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status INT DEFAULT 1
);

-- 创建角色表
CREATE TABLE ss_role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    `desc` VARCHAR(255),
    create_at DATETIME
);

-- 创建权限表
CREATE TABLE ss_authority (
    id INT PRIMARY KEY,
    parent_id INT,
    name VARCHAR(50),
    `desc` VARCHAR(255),
    resource VARCHAR(100),
    type INT,
    create_at DATETIME
);

-- 创建 OAuth2 客户端表
CREATE TABLE oauth_client_details (
    client_id VARCHAR(256) PRIMARY KEY,
    resource_ids VARCHAR(256),
    client_secret VARCHAR(256),
    scope VARCHAR(256),
    authorized_grant_types VARCHAR(256),
    web_server_redirect_uri VARCHAR(256),
    authorities VARCHAR(256),
    access_token_validity INTEGER,
    refresh_token_validity INTEGER,
    additional_information VARCHAR(4096),
    autoapprove VARCHAR(256)
);
```

### 启动服务

```bash
# 启动 Redis
redis-server

# 启动授权服务器
cd sso_server
mvn spring-boot:run
```

### 验证服务

```bash
# 访问 JWK Set 端点
curl http://localhost:8080/authorization-server/.well-known/jwks.json

# 密码模式获取令牌
curl -X POST "http://localhost:8080/authorization-server/oauth/token" \
  -H "Authorization: Basic d2ViLWNsaWVudDoxMjM0NTY3OA==" \
  -d "grant_type=password&username=admin&password=admin"
```

## 生成实体类

使用 Fluent-MyBatis 代码生成器：

```java
// 运行 EntityGeneratorDemo1.generate() 方法
// 自动生成 Entity、Mapper、Dao、Wrapper 等类
```

## 注意事项

1. **密钥库安全**: `jwt.jks` 文件包含私钥，生产环境应妥善保管
2. **密码编码**: 使用 BCrypt 编码，支持多种算法切换
3. **Token 存储**: 当前使用 Redis 存储，也可切换到数据库或 JWT 无状态模式
4. **调试模式**: 生产环境应关闭 `@EnableWebSecurity(debug = true)`

## 相关项目

- [sso_client_1](../sso_client_1) - 单点登录客户端
- [sso_resource_server](../sso_resource_server) - 资源服务器1
- [sso_resource_server2](../sso_resource_server2) - 资源服务器2
