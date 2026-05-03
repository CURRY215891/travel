package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("hotel_tag")
public class HotelTag {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
}
