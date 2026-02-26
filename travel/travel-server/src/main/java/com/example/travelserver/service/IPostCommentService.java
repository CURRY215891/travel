package com.example.travelserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.travelserver.entity.PostComment;
import java.util.List;
import java.util.Map;

public interface IPostCommentService extends IService<PostComment> {
    List<Map<String, Object>> getCommentsByPostId(Long postId);
}