package com.iunin.cloud.oauth.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{username}")
    User queryUserByUsername(String username);

    //@Select("select * from user")
    Collection<? extends GrantedAuthority> queryUserAuthorities(Integer id);
}
