package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.HotelBooking;
import com.example.travelserver.mapper.HotelBookingMapper;
import com.example.travelserver.service.IHotelBookingService;
import org.springframework.stereotype.Service;

@Service
public class HotelBookingServiceImpl extends ServiceImpl<HotelBookingMapper, HotelBooking> implements IHotelBookingService {
}
