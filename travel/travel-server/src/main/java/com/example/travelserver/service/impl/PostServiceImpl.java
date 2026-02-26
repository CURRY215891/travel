package com.example.travelserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.travelserver.entity.Post;
import com.example.travelserver.mapper.PostMapper;
import com.example.travelserver.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {

    @Autowired
    private PostMapper postMapper;

    // 必须实现这个方法，否则编译器会提示你把类改成 abstract
    @Override
    public List<Post> getPostListWithUser() {
        return postMapper.selectPostWithUser();
    }

    @Override
    public Post getByIdWithUserInfo(Long id) {
        // 调用 Mapper 中自定义的 SQL 方法
        return postMapper.selectPostWithUserById(id);
    }
}