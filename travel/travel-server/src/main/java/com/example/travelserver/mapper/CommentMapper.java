package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.*, u.nickname, u.avatar, " +
            "CASE " +
            "  WHEN c.type = 1 THEN a.name " +
            "  WHEN c.type = 2 THEN f.name " +
            "  WHEN c.type = 3 THEN r.name " +
            "END as attrName, " +
            "CASE " +
            "  WHEN c.type = 1 THEN a.mainImage " +
            "  WHEN c.type = 2 THEN f.main_image " +
            "  WHEN c.type = 3 THEN r.image " +
            "END as attrImage " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN attraction a ON c.attr_id = a.id AND c.type = 1 " +
            "LEFT JOIN food f ON c.attr_id = f.id AND c.type = 2 " +
            "LEFT JOIN hotel_room r ON c.attr_id = r.id AND c.type = 3 " +
            "WHERE c.attr_id = #{attrId} AND c.type = #{type} AND c.audit_status = 1 " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectByAttrId(@Param("attrId") Integer attrId, @Param("type") Integer type);

    @Select("SELECT c.*, " +
            "CASE " +
            "  WHEN c.type = 1 THEN a.name " +
            "  WHEN c.type = 2 THEN f.name " +
            "  WHEN c.type = 3 THEN r.name " +
            "END as attrName, " +
            "CASE " +
            "  WHEN c.type = 1 THEN a.mainImage " +
            "  WHEN c.type = 2 THEN f.main_image " +
            "  WHEN c.type = 3 THEN r.image " +
            "END as attrImage " +
            "FROM comment c " +
            "LEFT JOIN attraction a ON c.attr_id = a.id AND c.type = 1 " +
            "LEFT JOIN food f ON c.attr_id = f.id AND c.type = 2 " +
            "LEFT JOIN hotel_room r ON c.attr_id = r.id AND c.type = 3 " +
            "WHERE c.user_id = #{userId} AND c.type = #{type} ORDER BY c.create_time DESC")
    List<Comment> selectByUserIdAndType(@Param("userId") Integer userId, @Param("type") Integer type);

    @Select("SELECT c.*, u.nickname, u.avatar, " +
            "CASE " +
            "  WHEN c.type = 1 THEN a.name " +
            "  WHEN c.type = 2 THEN f.name " +
            "  WHEN c.type = 3 THEN r.name " +
            "END as attrName, " +
            "CASE " +
            "  WHEN c.type = 1 THEN a.mainImage " +
            "  WHEN c.type = 2 THEN f.main_image " +
            "  WHEN c.type = 3 THEN r.image " +
            "END as attrImage " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN attraction a ON c.attr_id = a.id AND c.type = 1 " +
            "LEFT JOIN food f ON c.attr_id = f.id AND c.type = 2 " +
            "LEFT JOIN hotel_room r ON c.attr_id = r.id AND c.type = 3 " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectAllWithAttrName();

    @Select("SELECT c.*, u.nickname, u.avatar, r.name as attrName, r.image as attrImage " +
            "FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN hotel_room r ON c.attr_id = r.id " +
            "WHERE c.type = 3 AND r.hotel_id = #{hotelId} AND c.audit_status = 1 " +
            "ORDER BY c.create_time DESC")
    List<Comment> selectHotelCommentsByHotelId(@Param("hotelId") Integer hotelId);
}
