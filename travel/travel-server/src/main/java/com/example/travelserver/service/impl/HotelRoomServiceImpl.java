package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.HotelRoom;
import com.example.travelserver.mapper.HotelRoomMapper;
import com.example.travelserver.service.IHotelRoomService;
import org.springframework.stereotype.Service;

@Service
public class HotelRoomServiceImpl extends ServiceImpl<HotelRoomMapper, HotelRoom> implements IHotelRoomService {
}
