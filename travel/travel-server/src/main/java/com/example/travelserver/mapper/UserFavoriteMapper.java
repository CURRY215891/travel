package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.UserFavorite;
import com.example.travelserver.entity.Post;
import com.example.travelserver.entity.Attraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 查询用户收藏的动态列表
     * 这里使用了 LEFT JOIN 关联 post 表，直接拿到动态的内容、图片等信息
     */
    @Select("SELECT p.*, u.nickname FROM user_favorite f " +
            "LEFT JOIN post p ON f.target_id = p.id " +
            "LEFT JOIN user u ON p.user_id = u.id " + // 关键点：关联用户表
            "WHERE f.user_id = #{userId} AND f.type = 1 " +
            "ORDER BY f.create_time DESC")
    List<Post> getFavoritePosts(Long userId);

    /**
     * 查询用户收藏的景点列表 (type = 2)
     * 注意：这里 categoryIds 应该是一个逗号分隔的字符串，为了安全，建议在业务层处理成 List 或使用 find_in_set
     */
    @Select("<script>" +
            "SELECT a.* FROM user_favorite f " +
            "LEFT JOIN attraction a ON f.target_id = a.id " +
            "WHERE f.user_id = #{userId} AND f.type = 2 " +
            "<if test='categoryIds != null and categoryIds != \"\"'> " +
            "  AND FIND_IN_SET(a.category_id, #{categoryIds}) " +
            "</if> " +
            "ORDER BY f.create_time DESC" +
            "</script>")
    List<Attraction> getFavoriteAttractions(@org.apache.ibatis.annotations.Param("userId") Long userId, @org.apache.ibatis.annotations.Param("categoryIds") String categoryIds);

    /**
     * 查询用户收藏的酒店列表 (type = 3)
     */
    @Select("SELECT h.* FROM user_favorite f " +
            "LEFT JOIN hotel h ON f.target_id = h.id " +
            "WHERE f.user_id = #{userId} AND f.type = 3 " +
            "ORDER BY f.create_time DESC")
    List<com.example.travelserver.entity.Hotel> getFavoriteHotels(@org.apache.ibatis.annotations.Param("userId") Long userId);

    /**
     * 查询用户收藏的美食列表 (type = 4)
     */
    @Select("SELECT fo.* FROM user_favorite f " +
            "LEFT JOIN food fo ON f.target_id = fo.id " +
            "WHERE f.user_id = #{userId} AND f.type = 4 " +
            "ORDER BY f.create_time DESC")
    List<com.example.travelserver.entity.Food> getFavoriteFoods(@org.apache.ibatis.annotations.Param("userId") Long userId);
}