package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String content;
    private String image;
    private Integer likes;
    private LocalDateTime createTime;
    private Integer commentCount;
    
    /**
     * 审核状态: 0-待审核, 1-已通过, 2-已拒绝
     */
    private Integer auditStatus;

    // 以下两个字段不在 post 表中，用于关联查询展示用户信息
    @TableField(exist = false)
    private String nickname;
    @TableField(exist = false)
    private String avatar;
}