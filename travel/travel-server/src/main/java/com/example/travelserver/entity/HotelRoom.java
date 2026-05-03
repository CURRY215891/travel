package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("hotel_room")
public class HotelRoom {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("hotel_id")
    private Integer hotelId;
    private String name;
    private String image;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String area;
    @TableField("bed_type")
    private String bedType;
    @TableField("`window`")
    private String window;
    private String facilities;
}
