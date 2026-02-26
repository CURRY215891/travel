package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.HotelBooking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HotelBookingMapper extends BaseMapper<HotelBooking> {

    @Select("SELECT b.*, h.name as hotel_name, r.name as room_name, r.image as room_image " +
            "FROM hotel_booking b " +
            "LEFT JOIN hotel h ON b.hotel_id = h.id " +
            "LEFT JOIN hotel_room r ON b.room_id = r.id " +
            "WHERE b.user_id = #{userId} " +
            "ORDER BY b.create_time DESC")
    List<HotelBooking> selectByUserId(@Param("userId") Integer userId);
}
