package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    // 1. 用于社交列表页：查询所有动态及其用户信息
    @Select("SELECT p.*, u.nickname, u.avatar FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "ORDER BY p.create_time DESC")
    List<Post> selectPostWithUser();

    // 2. 新增：用于动态详情页：根据 ID 查询单条动态及其用户信息
    @Select("SELECT p.*, u.nickname, u.avatar FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.id = #{id}")
    Post selectPostWithUserById(Long id);
}