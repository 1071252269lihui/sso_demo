# SSO Demo 项目指南

本项目是一个基于 Spring Security OAuth2 的单点登录演示系统。

## 项目结构

```
sso_demo/
├── sso_server/              # 授权服务器 (8080端口)
├── sso_resource_server/     # 资源服务器1 (8090端口)
├── sso_resource_server2/    # 资源服务器2 (8091端口)
└── sso_client_1/            # 客户端应用 (Angular + Zuul网关)
```

## 技术栈

- Spring Boot 2.5.5
- Spring Security OAuth2 (旧版API)
- JWT (RSA非对称加密)
- Fluent-MyBatis (ORM)
- Redis (Token存储)
- MySQL (用户数据)

## 核心配置

### 端口分配
| 模块 | 端口 | 上下文路径 |
|------|------|-----------|
| sso_server | 8080 | /authorization-server |
| sso_resource_server | 8090 | /resource_server |
| sso_resource_server2 | 8091 | /resource_server_2 |

### 关键端点

**授权服务器 (sso_server)**
- Token端点: `POST /oauth/token`
- 授权端点: `GET /oauth/authorize`
- JWK Set: `GET /.well-known/jwks.json`

**资源服务器**
- `/hello` - 需要 `api:hello` 权限
- `/user` - 返回当前用户信息

## 数据库表

- `ss_user` - 用户表
- `ss_role` - 角色表
- `ss_authority` - 权限表
- `ss_user_role_rel` - 用户-角色关联
- `ss_authority_role_rel` - 权限-角色关联
- `oauth_client_details` - OAuth客户端配置

## 启动顺序

1. Redis
2. sso_server
3. sso_resource_server
4. sso_resource_server2
5. sso_client_1

## 代码规范

### 包结构 (sso_server)
```
com.example.sso_server/
├── config/          # 安全配置、OAuth2配置
├── controller/      # 控制器
├── dao/             # 数据访问层
│   ├── impl/        # DAO实现
│   └── intf/        # DAO接口
├── entity/          # 实体类
├── service/         # 业务服务
└── wrapper/         # 查询包装器(自动生成)
```

### 安全配置要点

1. **密码编码**: 使用 `DelegatingPasswordEncoder`，默认BCrypt
2. **JWT签名**: RSA非对称加密，密钥存储在 `jwt.jks`
3. **权限控制**: 使用 `@PreAuthorize` 注解

### 资源服务器配置模式

```java
// WebSecurityConfig.java 核心配置
http
    .oauth2ResourceServer(resourceServer -> resourceServer.jwt()
        .jwtAuthenticationConverter(customJwtAuthenticationTokenConverter())
    );

// 自定义JWT转换器，合并authorities和scopes
private Converter<Jwt, AbstractAuthenticationToken> customJwtAuthenticationTokenConverter() {
    return jwt -> {
        List<String> userAuthorities = jwt.getClaimAsStringList("authorities");
        List<String> scopes = jwt.getClaimAsStringList("scope");
        // 合并权限...
    };
}
```

## 常见开发任务

### 添加新API端点

1. 在资源服务器创建Controller
2. 添加 `@PreAuthorize` 注解保护
3. 在 `ss_authority` 表添加对应权限

### 添加新资源服务器

1. 创建Spring Boot项目
2. 添加 `spring-boot-starter-oauth2-resource-server` 依赖
3. 配置 `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`
4. 实现 `WebSecurityConfig`

### 修改Token有效期

在 `AuthorizationServerConfig` 或数据库 `oauth_client_details` 表中配置。

## 注意事项

1. `jwt.jks` 密钥文件需妥善保管
2. 生产环境必须使用HTTPS
3. 注意Spring Security OAuth2旧版API已进入维护模式
