package com.example.travelserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.travelserver.entity.Comment;
import com.example.travelserver.entity.HotelRoom;
import com.example.travelserver.mapper.CommentMapper;
import com.example.travelserver.mapper.HotelRoomMapper;
import com.example.travelserver.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private HotelRoomMapper hotelRoomMapper;

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
            if (comment.getAuditStatus() == null) comment.setAuditStatus(1); // 默认直接通过
            
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

    // ==================== 超级管理员评论审核接口 ====================

    /**
     * 获取所有评论（包含待审核的）- 分页
     * type: 1-景点, 2-美食, 3-酒店房间
     * hotelId: 当type=3时，筛选指定酒店的评论
     */
    @GetMapping("/admin/list")
    public Map<String, Object> adminList(
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Comment> allComments = commentMapper.selectAllWithAttrName();
        
        var filtered = allComments.stream();
        
        if (auditStatus != null) {
            filtered = filtered.filter(c -> auditStatus.equals(c.getAuditStatus()));
        }
        if (type != null) {
            filtered = filtered.filter(c -> type.equals(c.getType()));
        }
        if (hotelId != null && type != null && type == 3) {
            List<Integer> roomIds = getRoomIdsByHotelId(hotelId);
            filtered = filtered.filter(c -> roomIds.contains(c.getAttrId()));
        }
        
        List<Comment> resultList = filtered.collect(java.util.stream.Collectors.toList());
        
        int total = resultList.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", resultList.subList(fromIndex, toIndex));
        result.put("total", total);
        
        return result;
    }

    /**
     * 根据酒店ID获取该酒店下所有房间的ID
     */
    private List<Integer> getRoomIdsByHotelId(Integer hotelId) {
        LambdaQueryWrapper<HotelRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelRoom::getHotelId, hotelId);
        List<HotelRoom> rooms = hotelRoomMapper.selectList(wrapper);
        return rooms.stream().map(HotelRoom::getId).collect(Collectors.toList());
    }

    /**
     * 审核通过评论
     */
    @PostMapping("/audit/approve/{id}")
    public boolean approveComment(@PathVariable Integer id) {
        Comment comment = commentService.getById(id);
        if (comment != null) {
            comment.setAuditStatus(1);
            return commentService.updateById(comment);
        }
        return false;
    }

    /**
     * 审核拒绝评论
     */
    @PostMapping("/audit/reject/{id}")
    public boolean rejectComment(@PathVariable Integer id) {
        Comment comment = commentService.getById(id);
        if (comment != null) {
            comment.setAuditStatus(2);
            return commentService.updateById(comment);
        }
        return false;
    }

    /**
     * 管理员删除评论
     */
    @DeleteMapping("/admin/{id}")
    public boolean adminDelete(@PathVariable Integer id) {
        return commentService.removeById(id);
    }

    /**
     * 酒店管理员查看本酒店的评论（客情反馈）- 分页
     */
    @GetMapping("/hotel/{hotelId}")
    public Map<String, Object> getHotelComments(
            @PathVariable Integer hotelId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Comment> allList = commentMapper.selectHotelCommentsByHotelId(hotelId);
        
        int total = allList.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", allList.subList(fromIndex, toIndex));
        result.put("total", total);
        
        return result;
    }
}
