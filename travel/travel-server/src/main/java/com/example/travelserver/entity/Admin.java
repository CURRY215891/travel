package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private Integer role; // 1-超级管理员, 2-酒店管理员
    private Integer hotelId; // 所属酒店ID (仅role=2时有效)
    private LocalDateTime createTime;
}
