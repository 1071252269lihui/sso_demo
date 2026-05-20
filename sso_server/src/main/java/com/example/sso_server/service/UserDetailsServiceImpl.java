package com.example.sso_server.service;

import cn.org.atool.fluent.mybatis.base.free.FreeQuery;
import com.example.sso_server.dao.impl.SsAuthorityDaoImpl;
import com.example.sso_server.dao.impl.SsUserDaoImpl;
import com.example.sso_server.entity.*;
import com.example.sso_server.wrapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SsUserDaoImpl ssUserDao;
    @Resource
    private SsAuthorityDaoImpl ssAuthorityDao;

    @SuppressWarnings("null")
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SsUserQuery ssUserQuery = new SsUserQuery()
                .selectAll()
                .where.username().eq(userName)
                .end();
        SsUserEntity ssUser = ssUserDao.findOne(ssUserQuery).orElseThrow(() -> new UsernameNotFoundException("未找到用户名为 " + userName + "的用户"));
        FreeQuery query = new FreeQuery(null).customizedByPlaceholder("SELECT a.*, a.resource as 'authority'\n" +
                "FROM ss_user u LEFT JOIN ss_user_role_rel ur ON u.id = ur.uid\n" +
                "LEFT JOIN ss_role r ON ur.rid = r.id\n" +
                "LEFT JOIN ss_authority_role_rel ar ON r.id = ar.role_id\n" +
                "LEFT JOIN ss_authority a ON ar.authority_id = a.id\n" +
                "WHERE\n" +
                "u.id = #{id}\n" +
                "and a.type = 1\n" +
                "GROUP BY a.id", ssUser);
        List<SsAuthorityEntity> authorityEntityList = ssAuthorityDao.mapper().listEntity(query);
        CustomUserDetailsEntity detailsEntity = new CustomUserDetailsEntity(authorityEntityList);
        BeanUtils.copyProperties(ssUser, detailsEntity);
        return detailsEntity;
    }
}
