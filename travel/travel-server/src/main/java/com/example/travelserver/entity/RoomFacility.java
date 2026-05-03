package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("system_facility")
public class RoomFacility {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer type; // 1-酒店设施, 2-房间设施
}
