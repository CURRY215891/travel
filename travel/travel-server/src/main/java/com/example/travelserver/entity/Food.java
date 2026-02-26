package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("food")
public class Food {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField("main_image")
    private String mainImage;
    private String images;
    private String description;
    private String address;
    @TableField("avg_price")
    private BigDecimal avgPrice;
    @TableField("business_hours")
    private String businessHours;
    private String latitude;
    private String longitude;
}
