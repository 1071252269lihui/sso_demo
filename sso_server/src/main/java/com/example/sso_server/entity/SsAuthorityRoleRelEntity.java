package com.example.sso_server.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SsAuthorityRoleRelEntity: 数据映射实体定义
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
    table = "ss_authority_role_rel",
    schema = "oauth2server"
)
public class SsAuthorityRoleRelEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 自增主键
   */
  @TableId("id")
  private Integer id;

  /**
   * 权限id
   */
  @TableField("authority_id")
  private Integer authorityId;

  /**
   * 角色id
   */
  @TableField("role_id")
  private Integer roleId;

  @Override
  public final Class entityClass() {
    return SsAuthorityRoleRelEntity.class;
  }
}
