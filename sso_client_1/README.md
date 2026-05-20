# SSO Client 1 - 单点登录客户端应用

基于 Spring Boot + Angular 的 OAuth2 单点登录客户端应用，集成了 Zuul API 网关。

## 技术栈

### 后端
- **Spring Boot**: 1.5.6.RELEASE
- **Spring Cloud OAuth2**: Dalston.SR2
- **Spring Security OAuth2**: 单点登录客户端
- **Zuul**: API 网关，路由请求到资源服务器

### 前端
- **Angular**: 16.0.0
- **Bootstrap**: 5.3.0
- **TypeScript**: 5.0.2
- **RxJS**: 7.8.0

## 功能特性

- OAuth2 授权码模式单点登录
- 自动认证状态检测
- 通过 Zuul 网关代理资源服务器请求
- 登录/登出功能

## 项目结构

```
sso_client_1/
├── src/
│   ├── main/
│   │   ├── java/com/example/sso_client_1/
│   │   │   └── SsoClient1Application.java    # Spring Boot 启动类
│   │   └── resources/
│   │       ├── application.yml               # 主配置文件
│   │       └── application-back.yml          # 备用配置
│   ├── app/                                   # Angular 应用
│   │   ├── app.component.ts                  # 主组件
│   │   ├── app.component.html                # 主模板
│   │   ├── app.component.css                 # 组件样式
│   │   └── app.module.ts                     # Angular 模块
│   ├── index.html                            # 入口 HTML
│   ├── main.ts                               # Angular 启动入口
│   └── styles.css                            # 全局样式
├── pom.xml                                   # Maven 配置
├── package.json                              # NPM 配置
└── angular.json                              # Angular CLI 配置
```

## 配置说明

### OAuth2 客户端配置

```yaml
security:
  oauth2:
    client:
      accessTokenUri: http://localhost:8080/authorization-server/oauth/token
      userAuthorizationUri: http://localhost:8080/authorization-server/oauth/authorize
      clientId: web-client
      clientSecret: 12345678
    resource:
      jwk:
        key-set-uri: http://localhost:8080/authorization-server/.well-known/jwks.json
```

### Zuul 路由配置

```yaml
zuul:
  routes:
    resource:
      path: /resource_server/**
      url: http://localhost:8090/resource_server/hello
    user:
      path: /user/**
      url: http://localhost:8091/resource_server_2/user
```

## 快速开始

### 前置条件

1. **JDK 8+**
2. **Node.js 18+**
3. **Maven 3.6+**
4. **授权服务器运行中** (sso_server, 端口 8080)
5. **资源服务器运行中** (sso_resource_server, sso_resource_server2)

### 安装依赖

```bash
# 安装 Node.js 依赖
npm install
```

### 开发模式

```bash
# 方式1: 分别启动前后端

# 启动 Angular 开发服务器
# 注意: Windows PowerShell 默认不加载当前目录命令，需使用 npx 或 npm scripts
npm start        # 推荐：使用 npm script
# 或
npx ng serve     # 使用 npx 执行本地 ng 命令
# 或
.\node_modules\.bin\ng serve  # 直接指定路径（Windows）

# 启动 Spring Boot 后端
mvn spring-boot:run

# 方式2: 使用 Maven 同时构建前后端
mvn clean install
mvn spring-boot:run
```

> **Windows PowerShell 用户注意**: `ng` 命令位于 `node_modules/.bin/` 目录，PowerShell 默认不从当前目录加载命令。请使用 `npm start`、`npx ng serve` 或 `.\node_modules\.bin\ng serve`。

### 生产构建

```bash
# 构建前端（输出到 target/classes/static）
ng build --configuration production

# 构建 JAR 包
mvn clean package

# 运行
java -jar target/sso_client_1-0.0.1-SNAPSHOT.jar
```

## 应用流程

### 登录流程

1. 用户访问应用首页
2. 应用检测用户认证状态 (`GET /user`)
3. 未认证用户点击 "Login"
4. 重定向到授权服务器登录页面
5. 用户授权后回调到客户端
6. 客户端获取 Access Token
7. 显示欢迎信息

### 登出流程

1. 用户点击 "Logout"
2. 调用 `POST /logout`
3. 清除认证状态
4. 显示登录提示

## API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/user` | GET | 获取当前用户信息，检测认证状态 |
| `/logout` | POST | 用户登出 |
| `/resource_server` | GET | 通过 Zuul 代理访问资源服务器1 |
| `/user/**` | GET | 通过 Zuul 代理访问资源服务器2 |

## 前端组件

### AppComponent

主应用组件，负责：
- 认证状态管理 (`authenticated`)
- 用户信息展示 (`greeting`)
- 登出操作 (`logout()`)
- 自动认证检测 (`authenticate()`)

```typescript
export class AppComponent {
  authenticated = false;
  greeting = {} as any;

  authenticate() {
    this.http.get('user').subscribe(response => {
      if (response['name']) {
        this.authenticated = true;
        this.http.get('resource_server').subscribe(data => this.greeting = data);
      }
    });
  }

  logout() {
    this.http.post('logout', {}).pipe(finalize(() => {
      this.authenticated = false;
    })).subscribe();
  }
}
```

## 构建配置

### Maven 构建流程

`pom.xml` 配置了 `frontend-maven-plugin`，在 Maven 构建时自动：
1. 安装 Node.js
2. 执行 `npm install`
3. 执行 `npm run build`

构建输出目录：`target/classes/static`

### Angular 构建配置

`angular.json` 配置：
- 输出目录：`target/classes/static`
- 开发模式：sourceMap 启用
- 生产模式：代码压缩、hash 文件名

## 注意事项

1. **版本兼容性**: 后端使用 Spring Boot 1.5.x，前端使用 Angular 16.x
2. **CORS**: 开发环境需要配置 CORS，生产环境通过 Zuul 代理解决
3. **CSRF**: 使用 `CookieCsrfTokenRepository` 保护 CSRF 攻击
4. **Session**: 客户端使用 Session 管理认证状态

## 故障排除

### 常见问题

1. **登录后无限重定向**
   - 检查 `clientId` 和 `clientSecret` 是否正确
   - 确认授权服务器已配置对应的客户端

2. **资源服务器请求失败**
   - 确认资源服务器正在运行
   - 检查 Zuul 路由配置

3. **Angular 构建失败**
   - 删除 `node_modules` 目录后重新 `npm install`
   - 检查 Node.js 版本是否兼容

4. **PowerShell 中 ng 命令找不到**
   - PowerShell 默认不从当前目录加载命令
   - 使用 `npm start` 或 `npx ng serve` 替代 `ng serve`
   - 或全局安装 Angular CLI: `npm install -g @angular/cli`

## 相关项目

- [sso_server](../sso_server) - OAuth2 授权服务器
- [sso_resource_server](../sso_resource_server) - 资源服务器1
- [sso_resource_server2](../sso_resource_server2) - 资源服务器2
