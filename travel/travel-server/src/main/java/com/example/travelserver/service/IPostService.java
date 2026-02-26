package com.example.travelserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.travelserver.entity.Post;
import java.util.List;

public interface IPostService extends IService<Post> {
    List<Post> getPostListWithUser();
    Post getByIdWithUserInfo(Long id);
}