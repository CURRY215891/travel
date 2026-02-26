package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 告诉 Spring Boot 这是一个数据库操作接口
public interface AttractionMapper extends BaseMapper<Attraction> {
    // 这里不需要写任何代码，BaseMapper 已经自带了增删改查功能
}