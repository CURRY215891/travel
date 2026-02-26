package com.example.travelserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.travelserver.entity.Food;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
}
