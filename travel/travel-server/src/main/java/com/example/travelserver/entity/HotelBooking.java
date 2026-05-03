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
    
    private Integer status; // 0:待支付, 1:待确认, 2:待入住, 3:已入住, 4:已完成, 5:已取消, 6:退款中, 7:已退款
    
    private String userName; // 预订人姓名
    
    private String userPhone; // 预订人电话
    
    private String remark; // 订单备注
    
    private LocalDateTime payTime; // 支付时间
    
    private LocalDateTime completeTime; // 核销/完成时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    private Integer userDeleted; // 用户是否删除: 0-否, 1-是

    // 辅助字段
    @TableField(exist = false)
    private String hotelName;
    @TableField(exist = false)
    private String roomName;
    @TableField(exist = false)
    private String roomImage;
}
