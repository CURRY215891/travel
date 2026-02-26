package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private Integer attrId;
    
    private Integer type; // 1: 景点, 2: 美食, 3: 酒店房间
    
    private String content;
    
    private Integer star; // 总体评分/酒店评分
    
    private Integer hygieneScore; // 房间卫生评分
    
    private Integer environmentScore; // 周边环境评分
    
    private Integer serviceScore; // 酒店服务评分
    
    private Integer facilityScore; // 设备设施评分
    
    private String images; // 评论图片，多张用逗号隔开
    
    private Integer parentId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 辅助字段
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String avatar;
    @TableField(exist = false)
    private String attrName;
    @TableField(exist = false)
    private String attrImage;
}
