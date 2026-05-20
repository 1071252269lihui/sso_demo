package com.example.sso_server.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SsUserEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@FluentMybatis(
    table = "ss_user",
    schema = "oauth2server"
)
public class SsUserEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   */
  @TableId("id")
  private Integer id;

  /**
   * 用户名
   */
  @TableField("username")
  private String username;

  /**
   * 密码
   */
  @TableField("password")
  private String password;

  /**
   * 状态
   */
  @TableField("status")
  private Integer status;

  @Override
  public final Class entityClass() {
    return SsUserEntity.class;
  }
}
