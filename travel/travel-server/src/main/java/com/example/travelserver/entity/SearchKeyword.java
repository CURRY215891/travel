package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("search_keyword")
public class SearchKeyword {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String keyword;

    private Integer searchCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
