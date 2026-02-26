package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post_like")
public class PostLike {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private Long userId;

    // 对应数据库的默认创建时间，MyBatis Plus 会自动映射
    private LocalDateTime createTime;
}