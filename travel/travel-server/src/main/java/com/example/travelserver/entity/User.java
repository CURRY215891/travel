package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String openid;

    private String nickname;

    private String avatar;

    /**
     * 用户状态: 0-正常, 1-封禁
     */
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;
}