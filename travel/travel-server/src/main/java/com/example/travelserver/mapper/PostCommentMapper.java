package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {

    // 获取详情页评论列表：关联查询用户信息
    @Select("SELECT pc.*, u.nickname, u.avatar " +
            "FROM post_comment pc " +
            "LEFT JOIN user u ON pc.user_id = u.id " +
            "WHERE pc.post_id = #{postId} " +
            "ORDER BY pc.create_time DESC")
    List<Map<String, Object>> selectCommentsWithUser(Long postId);
    //查询“我的评论”
    @Select("SELECT c.*, p.content as postContent FROM post_comment c " +
            "LEFT JOIN post p ON c.post_id = p.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.create_time DESC")
    List<Map<String, Object>> getMyCommentsWithPost(Long userId);

}