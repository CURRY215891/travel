package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.HotelTag;
import com.example.travelserver.mapper.HotelTagMapper;
import com.example.travelserver.service.IHotelTagService;
import org.springframework.stereotype.Service;

@Service
public class HotelTagServiceImpl extends ServiceImpl<HotelTagMapper, HotelTag> implements IHotelTagService {
}
