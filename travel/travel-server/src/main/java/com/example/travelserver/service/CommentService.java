package com.example.travelserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.travelserver.entity.Comment;
import java.util.List;

public interface CommentService extends IService<Comment> {
    List<Comment> getByAttrId(Integer attrId, Integer type);
    List<Comment> getByUserId(Integer userId, Integer type);
}
