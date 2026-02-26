package com.example.travelserver.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hotel_booking")
public class HotelBooking {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private Integer hotelId;
    
    private Integer roomId;
    
    private String checkInDate;
    
    private String checkOutDate;
    
    private BigDecimal totalPrice;
    
    private Integer status; // 0: 待支付, 1: 已支付, 2: 已取消, 3: 已完成
    
    private String userName; // 预订人姓名
    
    private String userPhone; // 预订人电话
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 辅助字段
    @TableField(exist = false)
    private String hotelName;
    @TableField(exist = false)
    private String roomName;
    @TableField(exist = false)
    private String roomImage;
}
