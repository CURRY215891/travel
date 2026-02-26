package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.PostComment;
import com.example.travelserver.mapper.PostCommentMapper;
import com.example.travelserver.service.IPostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment> implements IPostCommentService {

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Override
    public List<Map<String, Object>> getCommentsByPostId(Long postId) {
        return postCommentMapper.selectCommentsWithUser(postId);
    }
}