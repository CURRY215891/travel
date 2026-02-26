package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.travelserver.entity.Post;
import com.example.travelserver.entity.PostComment;
import com.example.travelserver.mapper.PostCommentMapper; // 确保导入了 Mapper
import com.example.travelserver.service.IPostCommentService;
import com.example.travelserver.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post-comment")
@CrossOrigin
public class PostCommentController {

    @Autowired
    private IPostCommentService postCommentService;

    @Autowired
    private IPostService postService;

    @Autowired
    private PostCommentMapper postCommentMapper; // 注入 Mapper 用于关联查询

    // 1. 获取动态对应的评论列表（你原有的功能）
    @GetMapping("/list/{postId}")
    public List<Map<String, Object>> list(@PathVariable Long postId) {
        return postCommentService.getCommentsByPostId(postId);
    }

    /**
     * 2. 新增：获取当前用户的评论记录列表
     * 关联了 post 表获取原文内容
     */
    @GetMapping("/myList")
    public List<Map<String, Object>> getMyComments(@RequestParam Long userId) {
        // 调用你在 PostCommentMapper 中编写的 getMyCommentsWithPost 方法
        return postCommentMapper.getMyCommentsWithPost(userId);
    }

    // 3. 发表评论（保留你原有的事务逻辑）
    @PostMapping("/add")
    @Transactional // 保证评论插入和计数增加是一个整体事务
    public boolean add(@RequestBody PostComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        boolean saved = postCommentService.save(comment);

        if (saved) {
            // 更新 post 表的 comment_count 字段 +1
            UpdateWrapper<Post> uw = new UpdateWrapper<>();
            uw.setSql("comment_count = comment_count + 1")
                    .eq("id", comment.getPostId());
            postService.update(uw);
        }
        return saved;
    }

    // 4. 删除评论
    @DeleteMapping("/{id}")
    @Transactional
    public boolean delete(@PathVariable Long id) {
        PostComment comment = postCommentService.getById(id);
        if (comment == null) return false;

        boolean removed = postCommentService.removeById(id);
        if (removed) {
            // 更新 post 表的 comment_count 字段 -1
            UpdateWrapper<Post> uw = new UpdateWrapper<>();
            uw.setSql("comment_count = GREATEST(0, comment_count - 1)")
                    .eq("id", comment.getPostId());
            postService.update(uw);
        }
        return removed;
    }
}