# SSO Demo - 单点登录演示项目

基于 Spring Security OAuth2 的单点登录(SSO)演示项目，实现了完整的授权服务器、资源服务器和客户端应用。

## 项目架构

```
sso_demo/
├── sso_server/              # 授权服务器 (端口: 8080)
├── sso_resource_server/     # 资源服务器1 (端口: 8090)
├── sso_resource_server2/    # 资源服务器2 (端口: 8091)
└── sso_client_1/            # 客户端应用 (Angular + Zuul网关)
```

## 技术栈

### 后端
- **Spring Boot**: 2.5.5
- **Spring Security OAuth2**: 实现OAuth2授权流程
- **JWT**: 使用RSA非对称加密签名令牌
- **Fluent-MyBatis**: ORM框架，简化数据库操作
- **Redis**: Token持久化存储
- **MySQL**: 用户、角色、权限数据存储

### 前端
- **Angular**: 16.0.3
- **Zuul**: API网关，路由请求到资源服务器

## 模块说明

### 1. sso_server (授权服务器)

核心功能：
- OAuth2授权码模式、密码模式等授权流程
- JWT令牌签发（RSA非对称加密）
- JWK Set端点暴露公钥（供资源服务器验签）
- 用户认证与权限加载

主要配置：
- 端口: `8080`
- 上下文路径: `/authorization-server`
- JWK端点: `/.well-known/jwks.json`
- Token端点: `/oauth/token`
- 授权端点: `/oauth/authorize`

数据库表结构：
- `ss_user`: 用户表
- `ss_role`: 角色表
- `ss_authority`: 权限表
- `ss_user_role_rel`: 用户-角色关联表
- `ss_authority_role_rel`: 权限-角色关联表
- `oauth_client_details`: OAuth2客户端配置表

### 2. sso_resource_server (资源服务器1)

核心功能：
- JWT令牌验证与解析
- 基于权限的访问控制
- RESTful API资源提供

配置：
- 端口: `8090`
- 上下文路径: `/resource_server`
- JWK Set URI: `http://localhost:8080/authorization-server/.well-known/jwks.json`

API端点：
- `GET /hello`: 需要 `api:hello` 权限

### 3. sso_resource_server2 (资源服务器2)

核心功能：
- 用户信息查询API
- JWT令牌验证

配置：
- 端口: `8091`
- 上下文路径: `/resource_server_2`

API端点：
- `GET /user`: 返回当前认证用户信息，需要 `api:hello` 权限

### 4. sso_client_1 (客户端应用)

核心功能：
- 单点登录客户端
- Zuul网关路由
- Angular前端界面

配置：
- OAuth2客户端ID: `web-client`
- 资源服务器路由:
  - `/resource_server/**` → `http://localhost:8090/resource_server/hello`
  - `/user/**` → `http://localhost:8091/resource_server_2/user`

## 快速开始

### 前置条件

1. **JDK 8+**
2. **Maven 3.6+**
3. **MySQL 8.0+**
4. **Redis**

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE oauth2server;
```

2. 创建必要的表（参考 `oauth_client_details` 等表结构）

3. 修改 `sso_server/src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oauth2server?...
    username: your_username
    password: your_password
```

### 启动顺序

1. **启动 Redis**
```bash
redis-server
```

2. **启动授权服务器**
```bash
cd sso_server
mvn spring-boot:run
```

3. **启动资源服务器1**
```bash
cd sso_resource_server
mvn spring-boot:run
```

4. **启动资源服务器2**
```bash
cd sso_resource_server2
mvn spring-boot:run
```

5. **启动客户端应用**
```bash
cd sso_client_1
mvn spring-boot:run
```

### 访问应用

- 客户端首页: `http://localhost:8080/`
- 授权服务器: `http://localhost:8080/authorization-server/`

## OAuth2 授权流程

### 授权码模式

```
1. 客户端重定向到授权端点
   GET /oauth/authorize?response_type=code&client_id=web-client&redirect_uri=...

2. 用户登录授权

3. 授权服务器返回授权码

4. 客户端使用授权码换取Token
   POST /oauth/token
   grant_type=authorization_code&code=...&redirect_uri=...

5. 资源服务器验证JWT Token并返回资源
```

### 密码模式

```bash
curl -X POST "http://localhost:8080/authorization-server/oauth/token" \
  -H "Authorization: Basic $(echo -n 'web-client:12345678' | base64)" \
  -d "grant_type=password&username=user&password=password"
```

## JWT 令牌结构

令牌包含以下声明：
- `user_name`: 用户名
- `authorities`: 用户权限列表
- `scope`: OAuth2作用域
- `exp`: 过期时间
- `jti`: 令牌唯一标识

## 安全配置说明

### 密码编码

使用 `DelegatingPasswordEncoder`，支持多种编码算法：
- 默认: BCrypt
- 可扩展支持其他算法（如SHA-1）

### JWT签名

使用RSA非对称加密：
- 私钥存储在 `jwt.jks` 密钥库中
- 公钥通过 `/.well-known/jwks.json` 端点暴露

### 权限控制

资源服务器使用 `@PreAuthorize` 注解进行方法级权限控制：
```java
@PreAuthorize("hasAnyAuthority('api:hello')")
public Message hello() { ... }
```

## 开发指南

### 添加新的资源服务器

1. 创建新的Spring Boot项目
2. 添加依赖：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

3. 配置JWK Set URI：
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8080/authorization-server/.well-known/jwks.json
```

### 添加新的权限

1. 在 `ss_authority` 表中插入新权限
2. 在资源服务器中使用 `@PreAuthorize` 注解保护API

## 注意事项

1. **密钥文件**: `jwt.jks` 文件需要妥善保管，生产环境应使用更安全的密钥管理方案
2. **CORS配置**: 生产环境需要配置适当的CORS策略
3. **HTTPS**: 生产环境必须使用HTTPS
4. **Token过期**: 根据业务需求调整Token过期时间

## 许可证

MIT License
