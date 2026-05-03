package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.RoomFacility;
import com.example.travelserver.mapper.RoomFacilityMapper;
import com.example.travelserver.service.IRoomFacilityService;
import org.springframework.stereotype.Service;

@Service
public class RoomFacilityServiceImpl extends ServiceImpl<RoomFacilityMapper, RoomFacility> implements IRoomFacilityService {
}
