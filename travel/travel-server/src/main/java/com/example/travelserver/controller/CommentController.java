package com.example.travelserver.controller;

import com.example.travelserver.entity.Comment;
import com.example.travelserver.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/attr/{attrId}")
    public List<Comment> listByAttrId(@PathVariable Integer attrId, @RequestParam(required = false, defaultValue = "1") Integer type) {
        return commentService.getByAttrId(attrId, type);
    }

    @GetMapping("/user/{userId}")
    public List<Comment> listByUserId(@PathVariable Integer userId, @RequestParam(required = false, defaultValue = "1") Integer type) {
        return commentService.getByUserId(userId, type);
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Comment comment) {
        if (comment == null || comment.getUserId() == null || comment.getAttrId() == null) {
            return false;
        }
        
        try {
            if (comment.getType() == null) comment.setType(1); // 默认景点
            
            // 查找该用户对该目标（景点/美食/房间）的首条评论（主评）
            // 使用 list 替代 one 更加安全，防止数据库中存在多条主评导致报错
            List<Comment> primaryComments = commentService.lambdaQuery()
                    .eq(Comment::getUserId, comment.getUserId())
                    .eq(Comment::getAttrId, comment.getAttrId())
                    .eq(Comment::getType, comment.getType())
                    .eq(Comment::getParentId, 0)
                    .orderByAsc(Comment::getCreateTime)
                    .list();

            if (primaryComments != null && !primaryComments.isEmpty()) {
                // 如果已有主评，新评论设为追评，关联到第一条主评上
                comment.setParentId(primaryComments.get(0).getId());
                comment.setStar(0); // 追评不计分
            } else {
                // 否则作为主评
                comment.setParentId(0);
                if (comment.getStar() == null) comment.setStar(5); // 默认满分
            }
            
            return commentService.save(comment);
        } catch (Exception e) {
            // 打印错误堆栈，方便在后端控制台查看具体原因（如：Unknown column 'type'）
            e.printStackTrace();
            return false;
        }
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return commentService.removeById(id);
    }

    @GetMapping("/check")
    public boolean checkRated(@RequestParam Integer userId, @RequestParam Integer attrId, @RequestParam(required = false, defaultValue = "1") Integer type) {
        return commentService.lambdaQuery()
                .eq(Comment::getUserId, userId)
                .eq(Comment::getAttrId, attrId)
                .eq(Comment::getType, type)
                .count() > 0;
    }
}
