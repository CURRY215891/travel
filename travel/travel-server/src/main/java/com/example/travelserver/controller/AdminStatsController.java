package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.travelserver.entity.*;
import com.example.travelserver.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/stats")
@CrossOrigin
public class AdminStatsController {

    @Autowired
    private IHotelBookingService bookingService;

    @Autowired
    private IAttractionService attractionService;

    @Autowired
    private IPostService postService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IHotelService hotelService;

    @Autowired
    private ISearchKeywordService searchKeywordService;

    /**
     * 获取全局统计概览
     */
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 用户总数
        long userCount = userService.count();
        
        // 订单总数
        long orderCount = bookingService.count();
        
        // 景点总数
        long attractionCount = attractionService.count();
        
        // 酒店总数
        long hotelCount = hotelService.count();
        
        // 动态总数
        long postCount = postService.count();
        
        // 总交易额
        List<HotelBooking> allBookings = bookingService.lambdaQuery()
                .ne(HotelBooking::getStatus, 3) // 排除已取消的
                .list();
        BigDecimal totalAmount = allBookings.stream()
                .map(HotelBooking::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        result.put("userCount", userCount);
        result.put("orderCount", orderCount);
        result.put("attractionCount", attractionCount);
        result.put("hotelCount", hotelCount);
        result.put("postCount", postCount);
        result.put("totalAmount", totalAmount);
        
        return result;
    }

    /**
     * 获取热门景点排行（按浏览量）
     */
    @GetMapping("/hot-attractions")
    public List<Attraction> getHotAttractions(@RequestParam(defaultValue = "10") int limit) {
        return attractionService.lambdaQuery()
                .orderByDesc(Attraction::getViewCount)
                .last("LIMIT " + limit)
                .list();
    }

    /**
     * 获取搜索热词统计
     */
    @GetMapping("/search-hotwords")
    public List<SearchKeyword> getSearchHotwords(@RequestParam(defaultValue = "10") int limit) {
        return searchKeywordService.lambdaQuery()
                .orderByDesc(SearchKeyword::getSearchCount)
                .last("LIMIT " + limit)
                .list();
    }

    /**
     * 获取财务统计 - 按日期统计交易额
     */
    @GetMapping("/finance/daily")
    public List<Map<String, Object>> getDailyFinance(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        List<HotelBooking> bookings;
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bookings = bookingService.lambdaQuery()
                    .between(HotelBooking::getCreateTime, start, end)
                    .ne(HotelBooking::getStatus, 3)
                    .list();
        } else {
            // 默认最近30天
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            bookings = bookingService.lambdaQuery()
                    .ge(HotelBooking::getCreateTime, thirtyDaysAgo)
                    .ne(HotelBooking::getStatus, 3)
                    .list();
        }

        // 按日期分组统计
        Map<String, BigDecimal> dailyStats = new TreeMap<>();
        for (HotelBooking booking : bookings) {
            String date = booking.getCreateTime().toLocalDate().toString();
            BigDecimal price = booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO;
            dailyStats.merge(date, price, BigDecimal::add);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : dailyStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("amount", entry.getValue());
            result.add(item);
        }
        return result;
    }

    /**
     * 获取订单统计 - 按状态统计
     */
    @GetMapping("/orders/status")
    public Map<String, Object> getOrderStatusStats() {
        Map<String, Object> result = new HashMap<>();
        
        // 状态: 0-待支付, 1-已支付, 2-已入住, 3-已取消, 4-退款中, 5-已退款
        String[] statusNames = {"待支付", "已支付", "已入住", "已取消", "退款中", "已退款"};
        
        for (int i = 0; i < statusNames.length; i++) {
            long count = bookingService.lambdaQuery()
                    .eq(HotelBooking::getStatus, i)
                    .count();
            result.put(statusNames[i], count);
        }
        
        return result;
    }

    /**
     * 获取订单统计 - 按日期统计订单数
     */
    @GetMapping("/orders/daily")
    public List<Map<String, Object>> getDailyOrders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        List<HotelBooking> bookings;
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            bookings = bookingService.lambdaQuery()
                    .between(HotelBooking::getCreateTime, start, end)
                    .list();
        } else {
            // 默认最近30天
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            bookings = bookingService.lambdaQuery()
                    .ge(HotelBooking::getCreateTime, thirtyDaysAgo)
                    .list();
        }

        // 按日期分组统计
        Map<String, Long> dailyStats = new TreeMap<>();
        for (HotelBooking booking : bookings) {
            String date = booking.getCreateTime().toLocalDate().toString();
            dailyStats.merge(date, 1L, Long::sum);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : dailyStats.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }

    /**
     * 获取各酒店营收统计
     */
    @GetMapping("/hotels/revenue")
    public List<Map<String, Object>> getHotelRevenue() {
        List<Hotel> hotels = hotelService.list();
        List<HotelBooking> allBookings = bookingService.lambdaQuery()
                .ne(HotelBooking::getStatus, 3)
                .list();
        
        // 按酒店ID分组
        Map<Integer, BigDecimal> hotelRevenue = new HashMap<>();
        for (HotelBooking booking : allBookings) {
            Integer hotelId = booking.getHotelId();
            BigDecimal price = booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO;
            hotelRevenue.merge(hotelId, price, BigDecimal::add);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Hotel hotel : hotels) {
            Map<String, Object> item = new HashMap<>();
            item.put("hotelId", hotel.getId());
            item.put("hotelName", hotel.getName());
            item.put("revenue", hotelRevenue.getOrDefault(hotel.getId(), BigDecimal.ZERO));
            result.add(item);
        }
        
        // 按营收降序排序
        result.sort((a, b) -> ((BigDecimal) b.get("revenue")).compareTo((BigDecimal) a.get("revenue")));
        
        return result;
    }

    /**
     * 获取用户增长统计
     */
    @GetMapping("/users/growth")
    public List<Map<String, Object>> getUserGrowth(@RequestParam(defaultValue = "30") int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<User> users = userService.lambdaQuery()
                .ge(User::getCreateTime, startDate)
                .list();
        
        // 按日期分组
        Map<String, Long> dailyCount = new TreeMap<>();
        for (User user : users) {
            String date = user.getCreateTime().toLocalDate().toString();
            dailyCount.merge(date, 1L, Long::sum);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : dailyCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", entry.getKey());
            item.put("count", entry.getValue());
            result.add(item);
        }
        return result;
    }
}
