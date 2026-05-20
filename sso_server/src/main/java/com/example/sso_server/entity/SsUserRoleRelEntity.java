package com.example.sso_server.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SsUserRoleRelEntity: 数据映射实体定义
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
    table = "ss_user_role_rel",
    schema = "oauth2server"
)
public class SsUserRoleRelEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 自增主键
   */
  @TableId("id")
  private Integer id;

  /**
   * 角色表id
   */
  @TableField("rid")
  private Integer rid;

  /**
   * 用户表id
   */
  @TableField("uid")
  private Integer uid;

  @Override
  public final Class entityClass() {
    return SsUserRoleRelEntity.class;
  }
}
