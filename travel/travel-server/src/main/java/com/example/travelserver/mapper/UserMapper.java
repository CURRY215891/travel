package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}