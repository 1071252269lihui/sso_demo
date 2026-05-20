package com.example.sso_server.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SsRoleEntity: 数据映射实体定义
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
    table = "ss_role",
    schema = "oauth2server"
)
public class SsRoleEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   */
  @TableId("id")
  private Integer id;

  /**
   * 角色名
   */
  @TableField("name")
  private String name;

  /**
   * 角色描述
   */
  @TableField("desc")
  private String desc;

  /**
   * 创建时间
   */
  @TableField("create_at")
  private Date createAt;

  @Override
  public final Class entityClass() {
    return SsRoleEntity.class;
  }
}
