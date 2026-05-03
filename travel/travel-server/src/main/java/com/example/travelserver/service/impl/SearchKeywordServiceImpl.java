package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.SearchKeyword;
import com.example.travelserver.mapper.SearchKeywordMapper;
import com.example.travelserver.service.ISearchKeywordService;
import org.springframework.stereotype.Service;

@Service
public class SearchKeywordServiceImpl extends ServiceImpl<SearchKeywordMapper, SearchKeyword> implements ISearchKeywordService {
}
