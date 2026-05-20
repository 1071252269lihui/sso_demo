package com.example.sso_server.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

/**
 * SsAuthorityEntity: 数据映射实体定义
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
    table = "ss_authority",
    schema = "oauth2server"
)
public class SsAuthorityEntity extends RichEntity implements GrantedAuthority {
  private static final long serialVersionUID = 1L;

  /**
   * 主键
   */
  @TableId(
      value = "id",
      auto = false
  )
  private Integer id;

  /**
   * 父权限id
   */
  @TableField("parent_id")
  private Integer parentId;

  /**
   * 权限名称
   */
  @TableField("name")
  private String name;

  /**
   * 权限描述
   */
  @TableField("desc")
  private String desc;

  /**
   * 权限资源,当type为1时有值
   */
  @TableField("resource")
  private String authority;

  /**
   * 权限类型。0：菜单，1：组件
   */
  @TableField("type")
  private Integer type;

  /**
   * 创建时间
   */
  @TableField("create_at")
  private Date createAt;

  @Override
  public final Class entityClass() {
    return SsAuthorityEntity.class;
  }
}
