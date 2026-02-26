package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.travelserver.entity.Post;
import com.example.travelserver.entity.PostComment;
import com.example.travelserver.entity.PostLike;
import com.example.travelserver.entity.UserFavorite;
import com.example.travelserver.mapper.PostCommentMapper;
import com.example.travelserver.mapper.PostLikeMapper;
import com.example.travelserver.mapper.UserFavoriteMapper;
import com.example.travelserver.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/post")
@CrossOrigin
public class PostController {

    @Autowired
    private IPostService postService;

    @Autowired
    private PostLikeMapper postLikeMapper; // 1. 注入你刚创建的 Mapper

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    // 获取所有动态（带用户信息）
    @GetMapping("/list")
    public List<Post> list() {
        return postService.getPostListWithUser();
    }

    // 删除动态（级联删除点赞、评论、收藏）
    @DeleteMapping("/{id}")
    @Transactional
    public boolean delete(@PathVariable Long id) {
        // 1. 删除点赞记录
        QueryWrapper<PostLike> likeQw = new QueryWrapper<>();
        likeQw.eq("post_id", id);
        postLikeMapper.delete(likeQw);

        // 2. 删除评论记录
        QueryWrapper<PostComment> commentQw = new QueryWrapper<>();
        commentQw.eq("post_id", id);
        postCommentMapper.delete(commentQw);

        // 3. 删除收藏记录 (type=1 表示动态)
        QueryWrapper<UserFavorite> favQw = new QueryWrapper<>();
        favQw.eq("target_id", id).eq("type", 1);
        userFavoriteMapper.delete(favQw);

        // 4. 删除动态本身
        return postService.removeById(id);
    }

//查询我的动态
    @GetMapping("/myList")
    public List<Post> getMyPosts(@RequestParam Long userId) {
        QueryWrapper<Post> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("create_time");
        return postService.list(qw);
    }


    // 获取动态详情
    @GetMapping("/detail/{id}")
    public Post getPostDetail(@PathVariable Long id) {
        return postService.getByIdWithUserInfo(id);
    }

    /**
     * 2. 修改后的点赞/取消点赞接口
     * 逻辑：自动判断。如果 post_like 表有记录则删除并减1，没有则插入并加1
     */
    @PostMapping("/like/{postId}/{userId}")
    @Transactional // 开启事务，保证两张表操作的原子性
    public boolean handleLike(@PathVariable Long postId, @PathVariable Long userId) {
        // 查询是否已存在点赞记录
        QueryWrapper<PostLike> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).eq("user_id", userId);
        PostLike existingLike = postLikeMapper.selectOne(wrapper);

        Post post = postService.getById(postId);
        if (post == null) return false;

        if (existingLike != null) {
            // 已点赞：执行“取消”逻辑
            postLikeMapper.deleteById(existingLike.getId());
            post.setLikes(Math.max(0, (post.getLikes() == null ? 0 : post.getLikes()) - 1));
        } else {
            // 未点赞：执行“点赞”逻辑
            PostLike newLike = new PostLike();
            newLike.setPostId(postId);
            newLike.setUserId(userId);
            postLikeMapper.insert(newLike);
            post.setLikes((post.getLikes() == null ? 0 : post.getLikes()) + 1);
        }

        return postService.updateById(post);
    }

    /**
     * 3. 新增：进入详情页时检查当前用户是否已点赞
     */
    @GetMapping("/like/check/{postId}/{userId}")
    public boolean checkLike(@PathVariable Long postId, @PathVariable Long userId) {
        QueryWrapper<PostLike> wrapper = new QueryWrapper<>();
        wrapper.eq("post_id", postId).eq("user_id", userId);
        return postLikeMapper.selectCount(wrapper) > 0;
    }

    // 发布新动态
    @PostMapping("/add")
    public boolean add(@RequestBody Post post) {
        post.setCreateTime(LocalDateTime.now());
        if (post.getLikes() == null) post.setLikes(0);
        return postService.save(post);
    }
}