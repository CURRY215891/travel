package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.travelserver.entity.HotelBooking;
import com.example.travelserver.entity.Hotel;
import com.example.travelserver.entity.HotelRoom;
import com.example.travelserver.service.IHotelBookingService;
import com.example.travelserver.service.IHotelService;
import com.example.travelserver.service.IHotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booking")
@CrossOrigin
public class HotelBookingController {

    @Autowired
    private IHotelBookingService bookingService;
    
    @Autowired
    private com.example.travelserver.mapper.HotelBookingMapper bookingMapper;

    @Autowired
    private IHotelService hotelService;

    @Autowired
    private IHotelRoomService roomService;

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        
        QueryWrapper<HotelBooking> qw = new QueryWrapper<>();
        qw.eq("room_id", booking.getRoomId())
          .and(wrapper -> wrapper
              .ne("status", 5)
              .ne("status", 7)
          )
          .and(wrapper -> wrapper
              .lt("check_in_date", booking.getCheckOutDate())
              .gt("check_out_date", booking.getCheckInDate())
          );
        
        long count = bookingService.count(qw);
        if (count > 0) {
            result.put("success", false);
            result.put("message", "该时间段房间已被预订");
            return result;
        }

        booking.setStatus(0);
        booking.setUserDeleted(0);
        boolean saved = bookingService.save(booking);
        
        if (saved) {
            result.put("success", true);
            result.put("bookingId", booking.getId());
        } else {
            result.put("success", false);
            result.put("message", "创建订单失败");
        }
        return result;
    }

    @GetMapping("/user/{userId}")
    public List<HotelBooking> getByUserId(@PathVariable Integer userId) {
        return bookingMapper.selectByUserId(userId);
    }

    @GetMapping("/hotel/{hotelId}")
    public Map<String, Object> getByHotelId(
            @PathVariable Integer hotelId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<HotelBooking> allList = bookingMapper.selectByHotelId(hotelId);
        
        int total = allList.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", allList.subList(fromIndex, toIndex));
        result.put("total", total);
        
        return result;
    }

    @PostMapping("/pay")
    public Map<String, Object> pay(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null || dbBooking.getStatus() != 0) {
            result.put("success", false);
            result.put("message", "订单状态不正确");
            return result;
        }
        
        dbBooking.setStatus(1);
        dbBooking.setPayTime(LocalDateTime.now());
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        return result;
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirm(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null || dbBooking.getStatus() != 1) {
            result.put("success", false);
            result.put("message", "订单状态不正确");
            return result;
        }
        
        dbBooking.setStatus(2);
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        return result;
    }

    @PostMapping("/check-in")
    public Map<String, Object> checkIn(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null || dbBooking.getStatus() != 2) {
            result.put("success", false);
            result.put("message", "订单状态不正确");
            return result;
        }
        
        dbBooking.setStatus(3);
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        return result;
    }

    @PostMapping("/check-out")
    public Map<String, Object> checkOut(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null || dbBooking.getStatus() != 3) {
            result.put("success", false);
            result.put("message", "订单状态不正确");
            return result;
        }
        
        dbBooking.setStatus(4);
        dbBooking.setCompleteTime(LocalDateTime.now());
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        return result;
    }

    @PostMapping("/cancel")
    public Map<String, Object> cancel(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        Integer status = dbBooking.getStatus();
        if (status != 0 && status != 1 && status != 2) {
            result.put("success", false);
            result.put("message", "该状态下无法取消订单");
            return result;
        }
        
        dbBooking.setStatus(5);
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        return result;
    }

    @PostMapping("/apply-refund")
    public Map<String, Object> applyRefund(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        Integer status = dbBooking.getStatus();
        if (status != 1 && status != 2) {
            result.put("success", false);
            result.put("message", "只能在办理入住前申请退款");
            return result;
        }
        
        dbBooking.setStatus(6);
        boolean updated = bookingService.updateById(dbBooking);
        result.put("success", updated);
        return result;
    }

    @PostMapping("/complete-refund")
    public Map<String, Object> completeRefund(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null || dbBooking.getStatus() != 6) {
            result.put("success", false);
            result.put("message", "订单状态不正确");
            return result;
        }
        
        dbBooking.setStatus(7);
        dbBooking.setCompleteTime(LocalDateTime.now());
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        return result;
    }

    @PostMapping("/user-delete")
    public Map<String, Object> userDelete(@RequestBody HotelBooking booking) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(booking.getId());
        if (dbBooking == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        dbBooking.setUserDeleted(1);
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        return result;
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return bookingService.removeById(id);
    }

    /**
     * 酒店营收统计
     */
    @GetMapping("/stats/{hotelId}")
    public Map<String, Object> getHotelStats(@PathVariable Integer hotelId) {
        Map<String, Object> result = new HashMap<>();
        
        List<HotelBooking> bookings = bookingMapper.selectByHotelId(hotelId);
        
        // 日营收统计（最近30天）
        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.Map<String, java.math.BigDecimal> dailyRevenue = new java.util.LinkedHashMap<>();
        for (int i = 29; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            for (HotelBooking b : bookings) {
                if (b.getPayTime() != null && b.getTotalPrice() != null) {
                    java.time.LocalDate payDate = b.getPayTime().toLocalDate();
                    if (payDate.equals(date)) {
                        total = total.add(b.getTotalPrice());
                    }
                }
            }
            dailyRevenue.put(dateStr, total);
        }
        result.put("dailyRevenue", dailyRevenue);
        
        // 月度营收统计（最近12个月）
        java.util.Map<String, java.math.BigDecimal> monthlyRevenue = new java.util.LinkedHashMap<>();
        for (int i = 11; i >= 0; i--) {
            java.time.LocalDate monthDate = today.minusMonths(i).withDayOfMonth(1);
            String monthStr = monthDate.getYear() + "-" + String.format("%02d", monthDate.getMonthValue());
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            for (HotelBooking b : bookings) {
                if (b.getPayTime() != null && b.getTotalPrice() != null) {
                    java.time.LocalDate payDate = b.getPayTime().toLocalDate();
                    String payMonth = payDate.getYear() + "-" + String.format("%02d", payDate.getMonthValue());
                    if (payMonth.equals(monthStr)) {
                        total = total.add(b.getTotalPrice());
                    }
                }
            }
            monthlyRevenue.put(monthStr, total);
        }
        result.put("monthlyRevenue", monthlyRevenue);
        
        // 订单成交趋势（最近30天订单数）
        java.util.Map<String, Integer> orderTrend = new java.util.LinkedHashMap<>();
        for (int i = 29; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            int count = 0;
            for (HotelBooking b : bookings) {
                if (b.getPayTime() != null) {
                    java.time.LocalDate payDate = b.getPayTime().toLocalDate();
                    if (payDate.equals(date)) {
                        count++;
                    }
                }
            }
            orderTrend.put(dateStr, count);
        }
        result.put("orderTrend", orderTrend);
        
        // 各房型入住率
        java.util.Map<String, Object> roomOccupancy = new java.util.HashMap<>();
        java.util.Map<Integer, String> roomNames = new java.util.HashMap<>();
        java.util.Map<Integer, Integer> roomBookings = new java.util.HashMap<>();
        
        for (HotelBooking b : bookings) {
            if (b.getRoomId() != null && b.getStatus() >= 2 && b.getStatus() <= 4) {
                roomBookings.put(b.getRoomId(), roomBookings.getOrDefault(b.getRoomId(), 0) + 1);
                if (b.getRoomName() != null) {
                    roomNames.put(b.getRoomId(), b.getRoomName());
                }
            }
        }
        
        java.util.List<java.util.Map<String, Object>> occupancyList = new java.util.ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : roomBookings.entrySet()) {
            java.util.Map<String, Object> roomData = new java.util.HashMap<>();
            roomData.put("roomId", entry.getKey());
            roomData.put("roomName", roomNames.getOrDefault(entry.getKey(), "未知房型"));
            roomData.put("bookingCount", entry.getValue());
            // 计算入住率（这里简化处理，实际应该根据可用天数计算）
            roomData.put("occupancyRate", Math.min(entry.getValue() * 10, 100));
            occupancyList.add(roomData);
        }
        result.put("roomOccupancy", occupancyList);
        
        return result;
    }

    // ==================== 超级管理员订单监控接口 ====================

    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminStats() {
        Map<String, Object> result = new HashMap<>();
        List<HotelBooking> allBookings = bookingService.list();
        LocalDate today = LocalDate.now();

        int todayOrderCount = 0;
        int pendingCount = 0;
        java.math.BigDecimal todayAmount = java.math.BigDecimal.ZERO;

        for (HotelBooking b : allBookings) {
            if (b.getCreateTime() != null && b.getCreateTime().toLocalDate().equals(today)) {
                todayOrderCount++;
            }
            if (b.getStatus() == 0 || b.getStatus() == 1 || b.getStatus() == 6) {
                pendingCount++;
            }
            if (b.getPayTime() != null && b.getPayTime().toLocalDate().equals(today) && b.getTotalPrice() != null) {
                todayAmount = todayAmount.add(b.getTotalPrice());
            }
        }

        result.put("todayOrderCount", todayOrderCount);
        result.put("pendingCount", pendingCount);
        result.put("todayAmount", todayAmount);

        Map<Integer, Integer> statusDistribution = new HashMap<>();
        for (HotelBooking b : allBookings) {
            Integer status = b.getStatus() != null ? b.getStatus() : 0;
            statusDistribution.put(status, statusDistribution.getOrDefault(status, 0) + 1);
        }
        result.put("statusDistribution", statusDistribution);

        Map<String, Integer> orderTrend = new java.util.LinkedHashMap<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            int count = 0;
            for (HotelBooking b : allBookings) {
                if (b.getCreateTime() != null && b.getCreateTime().toLocalDate().equals(date)) {
                    count++;
                }
            }
            orderTrend.put(dateStr, count);
        }
        result.put("orderTrend", orderTrend);

        return result;
    }

    @GetMapping("/admin/list")
    public Map<String, Object> getAdminList(
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Map<String, Object> result = new HashMap<>();
        
        QueryWrapper<HotelBooking> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time");
        
        if (hotelId != null) {
            qw.eq("hotel_id", hotelId);
        }
        if (status != null) {
            qw.eq("status", status);
        }
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge("create_time", startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le("create_time", endDate + " 23:59:59");
        }
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w
                .like("user_name", keyword)
                .or()
                .like("user_phone", keyword)
                .or()
                .like("id", keyword)
            );
        }

        long total = bookingService.count(qw);
        qw.last("LIMIT " + (page - 1) * pageSize + "," + pageSize);
        List<HotelBooking> list = bookingService.list(qw);

        List<Integer> hotelIds = list.stream().map(HotelBooking::getHotelId).distinct().collect(Collectors.toList());
        List<Integer> roomIds = list.stream().map(HotelBooking::getRoomId).distinct().collect(Collectors.toList());
        
        Map<Integer, String> hotelNameMap = new HashMap<>();
        Map<Integer, String> roomNameMap = new HashMap<>();
        
        if (!hotelIds.isEmpty()) {
            List<Hotel> hotels = hotelService.listByIds(hotelIds);
            for (Hotel h : hotels) {
                hotelNameMap.put(h.getId(), h.getName());
            }
        }
        if (!roomIds.isEmpty()) {
            List<HotelRoom> rooms = roomService.listByIds(roomIds);
            for (HotelRoom r : rooms) {
                roomNameMap.put(r.getId(), r.getName());
            }
        }

        for (HotelBooking b : list) {
            b.setHotelName(hotelNameMap.getOrDefault(b.getHotelId(), "未知酒店"));
            b.setRoomName(roomNameMap.getOrDefault(b.getRoomId(), "未知房型"));
        }

        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        
        return result;
    }

    @GetMapping("/admin/{id}")
    public HotelBooking getAdminDetail(@PathVariable Integer id) {
        HotelBooking booking = bookingService.getById(id);
        if (booking != null) {
            if (booking.getHotelId() != null) {
                Hotel hotel = hotelService.getById(booking.getHotelId());
                if (hotel != null) {
                    booking.setHotelName(hotel.getName());
                }
            }
            if (booking.getRoomId() != null) {
                HotelRoom room = roomService.getById(booking.getRoomId());
                if (room != null) {
                    booking.setRoomName(room.getName());
                    booking.setRoomImage(room.getImage());
                }
            }
        }
        return booking;
    }

    @PostMapping("/admin/{id}/cancel")
    public Map<String, Object> adminCancel(@PathVariable Integer id, @RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(id);
        if (dbBooking == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        Integer oldStatus = dbBooking.getStatus();
        dbBooking.setStatus(5);
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        result.put("oldStatus", oldStatus);
        result.put("newStatus", 5);
        return result;
    }

    @PostMapping("/admin/{id}/refund")
    public Map<String, Object> adminRefund(@PathVariable Integer id, @RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(id);
        if (dbBooking == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        Integer oldStatus = dbBooking.getStatus();
        dbBooking.setStatus(7);
        dbBooking.setCompleteTime(LocalDateTime.now());
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        result.put("oldStatus", oldStatus);
        result.put("newStatus", 7);
        return result;
    }

    @PostMapping("/admin/{id}/status")
    public Map<String, Object> adminUpdateStatus(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        HotelBooking dbBooking = bookingService.getById(id);
        if (dbBooking == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }
        
        Integer oldStatus = dbBooking.getStatus();
        Integer newStatus = Integer.parseInt(params.get("status").toString());
        dbBooking.setStatus(newStatus);
        
        if (newStatus == 4 || newStatus == 7) {
            dbBooking.setCompleteTime(LocalDateTime.now());
        }
        if (newStatus == 1 && dbBooking.getPayTime() == null) {
            dbBooking.setPayTime(LocalDateTime.now());
        }
        
        boolean updated = bookingService.updateById(dbBooking);
        
        result.put("success", updated);
        result.put("oldStatus", oldStatus);
        result.put("newStatus", newStatus);
        return result;
    }
}
