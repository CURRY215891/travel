package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("hotel")
public class Hotel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField("main_image")
    private String mainImage;
    private String images;
    private String description;
    private String address;
    @TableField("min_price")
    private BigDecimal minPrice;
    @TableField("star_level")
    private Integer starLevel;
    private String facilities;
    private String tags;
    private String latitude;
    private String longitude;
}
