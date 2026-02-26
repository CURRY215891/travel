package com.example.travelserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.travelserver.entity.Attraction;

// 继承 IService 是 MyBatis-Plus 的要求，它会自动提供 list() 和 getById() 方法
public interface IAttractionService extends IService<Attraction> {
}