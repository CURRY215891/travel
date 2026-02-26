package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.Comment;
import com.example.travelserver.mapper.CommentMapper;
import com.example.travelserver.service.CommentService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<Comment> getByAttrId(Integer attrId, Integer type) {
        return baseMapper.selectByAttrId(attrId, type);
    }

    @Override
    public List<Comment> getByUserId(Integer userId, Integer type) {
        return baseMapper.selectByUserIdAndType(userId, type);
    }
}
