package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;



@Data
@TableName("attraction")
public class Attraction {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("category_id")
    private Integer categoryId;

    private String name;

    @TableField("mainImage")
    private String mainImage;

    private String images;

    // 确定是 description
    private String description;

    private String address;

    private BigDecimal price;

    @TableField("openTime")
    private String openTime;

    private String latitude;

    private String longitude;

    @TableField("viewCount")
    private Integer viewCount;
}
