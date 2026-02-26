package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.Attraction;
import com.example.travelserver.mapper.AttractionMapper;
import com.example.travelserver.service.IAttractionService;
import org.springframework.stereotype.Service; // 必须导入这个包

@Service  // <--- 核心！检查这一行有没有漏掉
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements IAttractionService {
}