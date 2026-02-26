package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.travelserver.entity.Attraction;
import com.example.travelserver.entity.Post;
import com.example.travelserver.entity.UserFavorite;
import com.example.travelserver.mapper.UserFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
@CrossOrigin
public class UserFavoriteController {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper; // 记得创建对应的Mapper

    // 收藏或取消收藏
    @PostMapping("/toggle")
    public boolean toggle(@RequestBody UserFavorite fav) {
        QueryWrapper<UserFavorite> qw = new QueryWrapper<>();
        qw.eq("user_id", fav.getUserId())
                .eq("target_id", fav.getTargetId())
                .eq("type", fav.getType());

        UserFavorite old = userFavoriteMapper.selectOne(qw);
        if (old != null) {
            return userFavoriteMapper.deleteById(old.getId()) > 0;
        } else {
            return userFavoriteMapper.insert(fav) > 0;
        }
    }

    @GetMapping("/myList")
    public List<Post> getMyFavoritePosts(@RequestParam Long userId) {
        // 返回收藏的动态列表 (默认 type=1)
        return userFavoriteMapper.getFavoritePosts(userId);
    }

    @GetMapping("/myAttractions")
    public List<?> getMyFavoriteAttractions(@RequestParam Long userId, @RequestParam(required = false) String categoryIds) {
        // 根据 categoryIds 判断返回哪种类型
        if ("4".equals(categoryIds)) {
            return userFavoriteMapper.getFavoriteFoods(userId);
        } else if ("2".equals(categoryIds)) {
            return userFavoriteMapper.getFavoriteHotels(userId);
        }
        // 默认返回景点列表 (type=2)
        return userFavoriteMapper.getFavoriteAttractions(userId, categoryIds);
    }

    // 检查是否已收藏
    @GetMapping("/check")
    public boolean check(Long userId, Long targetId, Integer type) {
        QueryWrapper<UserFavorite> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("target_id", targetId).eq("type", type);
        return userFavoriteMapper.selectCount(qw) > 0;
    }
}