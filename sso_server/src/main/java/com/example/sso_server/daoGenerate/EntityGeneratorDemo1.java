package com.example.sso_server.daoGenerate;

import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Column;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import org.springframework.security.core.GrantedAuthority;

/**
 * Fluent-MyBatis Entity代码生成工具
 *
 * 注意：这是一个开发工具类，用于生成Entity代码，不是运行时代码。
 * 数据库连接信息在此硬编码是因为注解属性必须是编译时常量。
 *
 * 生产环境建议：
 * 1. 仅在开发环境运行此工具生成代码
 * 2. 生产数据库连接信息应通过 application.yml 的环境变量配置
 * 3. 如需修改连接信息，请直接修改下方常量值
 */
public class EntityGeneratorDemo1 {

    // 数据库连接信息常量（注解属性必须是编译时常量）
    // 生产环境：修改这些常量或使用开发环境数据库生成代码
    public static final String DB_HOST = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_NAME = "oauth2server";
    public static final String DB_USERNAME = "test";
    public static final String DB_PASSWORD = "test@2022_12";

    public static final String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
        + "?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC";

    public static void generate() throws Exception {
//        通过FileGenerator.build(Empty.class);调用，就可以在相应目录下生成Entity代码
        FileGenerator.build(EntityGeneratorDemo1.Empty.class);
    }

    @Tables(
            // 设置数据库连接信息
            url = url,
            username = DB_USERNAME,
            password = DB_PASSWORD,
            // 设置entity类生成src目录, 相对于 user.dir
            srcDir = "src/main/java",
            // 设置entity类的package值
            basePack = "com.example.sso_server",
            // 设置dao接口和实现的src目录, 相对于 user.dir
            daoDir = "src/main/java",
            // 设置哪些表要生成Entity文件
            tables = {
                    @Table(value = {"ss_user_role_rel","ss_role","ss_authority_role_rel","ss_user"})
                    ,@Table(value = {"ss_authority"},entity = GrantedAuthority.class,columns = {@Column(value = "resource:authority")})
            },
            alphabetOrder = false
    )
    static class Empty {
    }
}
